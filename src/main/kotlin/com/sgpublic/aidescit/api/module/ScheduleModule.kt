package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import com.sgpublic.aidescit.api.core.util.Log
import com.sgpublic.aidescit.api.data.ScheduleData
import com.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import com.sgpublic.aidescit.api.exceptions.ServiceUnavailableException
import com.sgpublic.aidescit.api.mariadb.dao.ClassChartRepository
import com.sgpublic.aidescit.api.mariadb.dao.ClassScheduleRepository
import com.sgpublic.aidescit.api.mariadb.domain.ClassSchedule
import com.sgpublic.aidescit.api.mariadb.domain.UserInfo
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * 课表模块
 */
@Component
class ScheduleModule {
    @Autowired
    private lateinit var schedule: ClassScheduleRepository
    @Autowired
    private lateinit var info: UserInfoModule
    @Autowired
    private lateinit var classChart: ClassChartRepository
    @Autowired
    private lateinit var session: SessionModule

    /**
     * 获取用户课表
     * @param username 用户学号/工号
     * @param year 学年
     * @param semester 学期
     */
    fun get(username: String, year: String = SemesterInfoProperty.YEAR,
            semester: Short = SemesterInfoProperty.SEMESTER): ScheduleData {
        val userInfo = info.get(username)
        val data = schedule.getSchedule(
            userInfo.faculty, userInfo.specialty, userInfo.classId, year, semester
        ) ?: return refresh(username, year, semester)
        return if (data.isExpired()){
            refresh(username, year, semester)
        } else {
            data.getContent()
        }
    }

    /**
     * 从教务系统刷新用户课表
     * @param username 用户学号/工号
     * @param year 学年
     * @param semester 学期
     */
    private fun refresh(username: String, year: String, semester: Short): ScheduleData {
        Log.d("刷新课表", username)
        val user = info.get(username)
        val session = session.get(username).session
        return when (user.identify){
            "0".toShort() -> {
                refreshStudent(user, year, semester, session)
            }
            "1".toShort() -> {
                refreshTeacher(user, year, semester, session)
            }
            else -> throw ServerRuntimeException.INTERNAL_ERROR
        }
    }

    /**
     * 获取学生课表
     */
    private fun refreshStudent(user: UserInfo, year: String, semester: Short, session: String): ScheduleData {
        val url = "http://218.6.163.93:8081/tjkbcx.aspx?xh=${user.username}"
        var viewstate = APIModule.executeDocument(
            url = url,
            headers = APIModule.buildHeaders(
                "Referer" to url
            ),
            cookies = APIModule.buildCookies(
                APIModule.COOKIE_KEY to session
            ),
            method = APIModule.METHOD_GET
        ).viewstate

        var doc = APIModule.executeDocument(
            url = url,
            headers = APIModule.buildHeaders(
                "Referer" to url
            ),
            cookies = APIModule.buildCookies(
                APIModule.COOKIE_KEY to session
            ),
            body = APIModule.buildFormBody(
                "__EVENTTARGET" to "zy",
                "__EVENTARGUMENT" to "",
                "__LASTFOCUS" to "",
                "__VIEWSTATE" to viewstate,
                "__VIEWSTATEGENERATOR" to "3189F21D",
                "xn" to year,
                "xq" to semester,
                "nj" to user.grade,
                "xy" to user.faculty,
                "zy" to user.specialty,
                "kb" to "",
            ),
            method = APIModule.METHOD_POST
        )
        viewstate = doc.viewstate

        val className = this.classChart.getClassName(
            user.faculty, user.specialty, user.classId, user.grade
        ) ?: throw ServerRuntimeException.INTERNAL_ERROR

        var selected = false
        val tableId = doc.document.select("#kb").select("option").run {
            forEach { element ->
                if (element.text() != className){
                    return@forEach
                }
                if (!element.hasAttr("selected")){
                    return@forEach
                }
                selected = true
                return@run element.attr("value").apply {
                    if (this == ""){
                        throw ServerRuntimeException("tableId获取失败")
                    }
                }
            }
            throw ServerRuntimeException("tableId获取失败")
        }
        if (!selected){
            doc = APIModule.executeDocument(
                url = url,
                headers = APIModule.buildHeaders(
                    "Referer" to url
                ),
                cookies = APIModule.buildCookies(
                    APIModule.COOKIE_KEY to session
                ),
                body = APIModule.buildFormBody(
                    "__EVENTTARGET" to "kb",
                    "__EVENTARGUMENT" to "",
                    "__LASTFOCUS" to "",
                    "__VIEWSTATE" to viewstate,
                    "__VIEWSTATEGENERATOR" to "3189F21D",
                    "xn" to year,
                    "xq" to semester,
                    "nj" to user.grade,
                    "xy" to user.faculty,
                    "zy" to user.specialty,
                    "kb" to tableId,
                ),
                method = APIModule.METHOD_POST
            )
            doc.document.select("#kb").select("option").run {
                forEach { element ->
                    if (element.text() != className){
                        return@forEach
                    }
                    if (element.hasAttr("selected")){
                        return@run
                    }
                }
                throw ServerRuntimeException("无法选中目标课表数据")
            }
        }
        parseSchedule(doc.document).also {
            schedule.save(ClassSchedule().apply {
                this.id = tableId
                this.faculty = user.faculty
                this.specialty = user.specialty
                this.classId = user.classId
                this.grade = user.grade
                this.year = year
                this.semester = semester
                this.content = it.toString()
            })
            return it
        }
    }

    /**
     * 获取教师日程表
     */
    @Suppress("UNUSED_PARAMETER")
    private fun refreshTeacher(user: UserInfo, year: String, semester: Short, session: String): ScheduleData {
        throw ServiceUnavailableException()
    }

    /**
     * 解析课表数据
     */
    private fun parseSchedule(doc: Document): ScheduleData {
        val result = ScheduleData()
        var resultCount = 0
        doc.getElementById("Table6").getElementsByTag("tbody").select("tr").forEachIndexed { trIndex, tr ->
            tr.select("td").forEachIndexed tdFor@{ tdIndex, td ->
                val html: String = td.html().replace("\n", "").run {
                    return@run Pattern.compile("<font color=\"red\">(.*?)</font>")
                        .matcher(this).replaceAll("")
                }

                if (html == "&nbsp;") {
                    return@tdFor
                }
                if (td.attr("rowspan") != "2" || html.split("<br>").size < 2) {
                    return@tdFor
                }
                val positionEntry = when(tdIndex - trIndex / 2 % 2) {
                    1 -> result.monday
                    2 -> result.tuesday
                    3 -> result.wednesday
                    4 -> result.thursday
                    5 -> result.friday
                    6 -> result.saturday
                    7 -> result.sunday
                    else -> {
                        throw ServerRuntimeException.INTERNAL_ERROR
                    }
                }.run {
                    return@run when(trIndex / 2){
                        1 -> am1
                        2 -> am2
                        3 -> pm1
                        4 -> pm2
                        5 -> ev
                        else -> {
                            Log.d(trIndex / 2)
                            throw ServerRuntimeException.INTERNAL_ERROR
                        }
                    }
                }
                html.split("<br><br><br>").forEach { content ->
                    val item = ScheduleData.Companion.ScheduleItem().apply {
                        val singleData = content.split("<br>")
                        this.name = singleData[0]
                        val stringClass = singleData[1].run {
                            return@run substring(0, indexOf("("))
                                .replace("单", "")
                                .replace("双", "")
                        }
                        val weekRange0 = singleData[1].contains("双")
                        val weekRange1 = singleData[1].contains("单")
                        if (stringClass.contains(",")) {
                            stringClass.split(",")
                        } else {
                            listOf(stringClass)
                        }.forEach { ranges ->
                            val localRange = if (ranges.contains("-")){
                                ranges.split("-")
                            } else {
                                listOf(ranges, ranges)
                            }
                            for (rangeIndex in localRange[0].toShort() .. localRange[1].toShort()){
                                val index = rangeIndex % 2 == 0
                                if (weekRange0 && !index){
                                    continue
                                }
                                if (weekRange1 && index){
                                    continue
                                }
                                this.range.add(rangeIndex.toShort())
                            }
                        }
                        this.teacher = singleData[2]
                        this.room = singleData[3]
                    }
                    if (positionEntry.contains(item)){
                        positionEntry[positionEntry.indexOf(item)].range.addAll(item.range)
                    } else {
                        positionEntry.add(item)
                    }
                    resultCount++
                }
            }
        }

        return result
    }
}