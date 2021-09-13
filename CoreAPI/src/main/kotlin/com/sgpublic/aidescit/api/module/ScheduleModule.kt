package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import com.sgpublic.aidescit.api.core.util.Log
import com.sgpublic.aidescit.api.data.ScheduleData
import com.sgpublic.aidescit.api.data.ViewStateDocument
import com.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import com.sgpublic.aidescit.api.exceptions.ServiceUnavailableException
import com.sgpublic.aidescit.api.mariadb.dao.ClassChartRepository
import com.sgpublic.aidescit.api.mariadb.dao.ClassScheduleRepository
import com.sgpublic.aidescit.api.mariadb.domain.ClassSchedule
import com.sgpublic.aidescit.api.mariadb.domain.UserInfo
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
        var doc = APIModule.executeDocument(
            url = url,
            headers = APIModule.buildHeaders(
                "Referer" to url
            ),
            cookies = APIModule.buildCookies(
                APIModule.Cookies.SESSION_ID to session
            ),
            method = APIModule.METHOD_GET
        )

        if (!doc.checkSelectedOption("#xn", year)){
            Log.d("课表学年未选中")
            doc = doc.post(
                "__EVENTTARGET" to "xn",
                "xn" to year,
                "xq" to doc.getSelectedOption("#xq"),
                "nj" to doc.getSelectedOption("#nj"),
                "xy" to doc.getSelectedOption("#xy"),
                "zy" to doc.getSelectedOption("#zy"),
                "kb" to doc.getSelectedOption("#kb"),
            )
            if (!doc.checkSelectedOption("#xn", year)){
                throw ServerRuntimeException("无法选中目标学年")
            }
        }

        if (!doc.checkSelectedOption("#xq", semester.toString(10))){
            Log.d("课表学期未选中")
            doc = doc.post(
                "__EVENTTARGET" to "xq",
                "xn" to year,
                "xq" to semester,
                "nj" to doc.getSelectedOption("#nj"),
                "xy" to doc.getSelectedOption("#xy"),
                "zy" to doc.getSelectedOption("#zy"),
                "kb" to doc.getSelectedOption("#kb"),
            )
            if (!doc.checkSelectedOption("#xq", semester.toString(10))){
                throw ServerRuntimeException("无法选中目标学期")
            }
        }

        if (!doc.checkSelectedOption("#xy", user.faculty.toString(10))) {
            Log.d("课表学院未选中")
            doc = doc.post(
                "__EVENTTARGET" to "zy",
                "xn" to year,
                "xq" to semester,
                "nj" to user.grade,
                "xy" to user.faculty,
                "zy" to doc.getSelectedOption("#zy"),
                "kb" to doc.getSelectedOption("#kb"),
            )
            if (!doc.checkSelectedOption("#xy", user.faculty.toString(10))) {
                throw ServerRuntimeException("无法选中目标学院")
            }
        }

        if (!doc.checkSelectedOption("#zy", user.specialty.toString(10))) {
            Log.d("课表专业未选中")
            doc = doc.post(
                "__EVENTTARGET" to "zy",
                "xn" to year,
                "xq" to semester,
                "nj" to user.grade,
                "xy" to user.faculty,
                "zy" to user.specialty,
                "kb" to doc.getSelectedOption("#kb"),
            )
            if (!doc.checkSelectedOption("#zy", user.specialty.toString(10))) {
                throw ServerRuntimeException("无法选中目标专业")
            }
        }

        val className = this.classChart.getClassName(
            user.faculty, user.specialty, user.classId, user.grade
        ) ?: throw ServerRuntimeException.INTERNAL_ERROR

        var selected = false
        val tableId: String = doc.select("#kb").run {
            select("option").forEach { element ->
                if (element.text() != className){
                    return@forEach
                }
                val idString: String = element.attr("value")
                if (idString == ""){
                    return@forEach
                }
                if (element.hasAttr("selected")){
                    selected = true
                }
                return@run idString
            }
            throw ServerRuntimeException("tableId获取失败")
        }
        if (!selected){
            doc = doc.post(
                "__EVENTTARGET" to "kb",
                "xn" to year,
                "xq" to semester,
                "nj" to user.grade,
                "xy" to user.faculty,
                "zy" to user.specialty,
                "kb" to tableId,
            )
            doc.select("#kb").select("option").run {
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
        parseSchedule(doc).let {
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
    private fun parseSchedule(doc: ViewStateDocument): ScheduleData {
        val result = ScheduleData()
        var resultCount = 0
        val classIndexPattern = Pattern.compile("(0|[1-9][0-9]*)")
        val classInfoPattern = Pattern.compile("<font color=\"red\">(.*?)</font>")
        val trs = doc.getElementById("Table6")!!
            .getElementsByTag("tbody")
            .select("tr")
        trs.forEachIndexed trFor@{ trIndex, tr ->
            val tds = tr.select("td")
            if (tds.size < 8){
                return@trFor
            }
            val tdIndexVar = tds.size - 8
            val classIndexMatcher = classIndexPattern.matcher(tds[tdIndexVar].text())
            if (!classIndexMatcher.find()){
                return@trFor
            }
            if ((classIndexMatcher.group(0).toIntOrNull() ?: 0) % 2 == 0){
                return@trFor
            }
            tds.forEachIndexed tdFor@{ tdIndex, td ->
                val classInfo = td.html().replace("\n", "")
                if (!classInfo.contains("<br>")){
                    return@tdFor
                }
                classInfoPattern.matcher(classInfo).replaceAll("").split("<br><br><br>").forEach { content ->
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
                    val classEntry = when(tdIndex + 1 - tdIndexVar) {
                        2 -> result.monday
                        3 -> result.tuesday
                        4 -> result.wednesday
                        5 -> result.thursday
                        6 -> result.friday
                        7 -> result.saturday
                        8 -> result.sunday
                        else -> {
                            return@tdFor
                        }
                    }.run {
                        return@run when(trIndex){
                            2 -> am1
                            4 -> am2
                            6 -> pm1
                            8 -> pm2
                            10 -> ev
                            else -> {
                                return@trFor
                            }
                        }
                    }
                    if (classEntry.contains(item)){
                        classEntry[classEntry.indexOf(item)].range.addAll(item.range)
                    } else {
                        classEntry.add(item)
                        resultCount++
                    }
                }
            }
        }

        return result
    }
}