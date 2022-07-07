package io.github.sgpublic.aidescit.api.module

import io.github.sgpublic.aidescit.api.Application
import io.github.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import io.github.sgpublic.aidescit.api.core.util.Log
import io.github.sgpublic.aidescit.api.data.ClassInfo
import io.github.sgpublic.aidescit.api.data.ScheduleData
import io.github.sgpublic.aidescit.api.data.ViewStateDocument
import io.github.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import io.github.sgpublic.aidescit.api.exceptions.ServiceUnavailableException
import io.github.sgpublic.aidescit.api.mariadb.dao.*
import io.github.sgpublic.aidescit.api.mariadb.domain.*
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*
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
    private lateinit var facultyChart: FacultyChartRepository
    @Autowired
    private lateinit var specialtyChart: SpecialtyChartRepository
    @Autowired
    private lateinit var session: SessionModule

    /**
     * 获取用户课表
     * @param username 用户学号/工号
     * @param year 学年
     * @param semester 学期
     */
    fun get(username: String, year: String = SemesterInfoProperty.YEAR,
            semester: Short = SemesterInfoProperty.SEMESTER,
            classInfo: ClassInfo): ScheduleData {
        val userInfo = info.get(username)
        val clazz = userInfo.takeIf { classInfo.isNull() } ?: classInfo
        val data: ClassSchedule? = schedule.getSchedule(
            clazz.faculty, clazz.specialty, clazz.classId,
            clazz.grade, year, semester
        )
        if (data == null || data.isExpired()) {
            refresh(userInfo)
        }
        return data?.getContent() ?: throw ServiceUnavailableException()
    }

    @Autowired
    private lateinit var thread: ThreadPoolTaskExecutor
    private var studentRefreshing = false
    private var teacherRefreshing = false
    /**
     * 从教务系统刷新所有课表
     * @param info 用户信息
     */
    private fun refresh(info: UserInfo) {
        // 高并发锁
        synchronized(lock) {
            if (APIModule.TS < studentUpdate ||
                studentRefreshing || teacherRefreshing) {
                return@synchronized
            }
            when {
                info.isStudent() -> studentRefreshing = true
                info.isTeacher() -> teacherRefreshing = true
            }
            thread.execute {
                Log.d("执行数据刷新")
                // 下一次刷新时间为第二天 2：00
                val current = Calendar.getInstance().also {
                    it.set(Calendar.HOUR, 2)
                    it.set(Calendar.MINUTE, 0)
                    it.set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                when {
                    info.isStudent() -> {
                        Log.i("刷新所有课表")
                        try {
                            refreshStudent(info, session.get(info.username)).takeIf { it }
                                ?: throw ServerRuntimeException("部分课表未获取到数据")
                            // 刷新成功则更新时间
                            studentUpdate = current + REFRESH_DELAY
                            Log.i("课表刷新完成")
                        } catch (e: Exception) {
                            studentUpdate += RETRY_DELAY
                            Log.w("课表刷新失败，将稍后重试", e)
                        }
                        studentRefreshing = false
                    }
                    info.isTeacher() -> {
                        Log.i("刷新所有日程表")
                        try {
                            refreshTeacher(info, session.get(info.username).session).takeIf { it }
                                ?: throw ServerRuntimeException("部分日程表未获取到数据")
                            // 刷新成功则更新时间
                            teacherUpdate = current + REFRESH_DELAY
                            Log.i("日程表刷新完成")
                        } catch (e: Exception) {
                            teacherUpdate += RETRY_DELAY
                            Log.w("日程表刷新失败，将稍后重试", e)
                        }
                        teacherRefreshing = false
                    }
                    else -> Log.w("未知身份信息")
                }
            }
        }
    }

    @Autowired
    private lateinit var room: ClassRoomRepository
    /** 获取学生课表 */
    private fun refreshStudent(user: UserInfo, session: UserSession): Boolean {
        val delay: Long = 50 // 请求延时
        var result = true
        val url = "http://218.6.163.93:8081/tjkbcx.aspx?xh=${user.username}"
        var doc = APIModule.executeDocument(
                url = url,
                headers = APIModule.buildHeaders(
                        "Referer" to url
                ),
                cookies = session.getCookie(),
                method = APIModule.METHOD_GET
        )
        Thread.sleep(delay)

        // 选中当前学年
        if (!doc.checkSelectedOption("#xn", SemesterInfoProperty.YEAR)){
            doc = doc.post(
                    "__EVENTTARGET" to "xn",
                    "xn" to SemesterInfoProperty.YEAR,
                    "xq" to doc.getSelectedOption("#xq"),
                    "nj" to doc.getSelectedOption("#nj"),
                    "xy" to doc.getSelectedOption("#xy"),
                    "zy" to doc.getSelectedOption("#zy"),
                    "kb" to doc.getSelectedOption("#kb"),
            )
            if (!doc.checkSelectedOption("#xn", SemesterInfoProperty.YEAR)){
                throw ServerRuntimeException("无法选中目标学年")
            }
            Thread.sleep(delay)
        }

        // 选中当前学期
        if (!doc.checkSelectedOption("#xq", SemesterInfoProperty.SEMESTER.toString(10))){
            doc = doc.post(
                    "__EVENTTARGET" to "xq",
                    "xn" to SemesterInfoProperty.YEAR,
                    "xq" to SemesterInfoProperty.SEMESTER,
                    "nj" to doc.getSelectedOption("#nj"),
                    "xy" to doc.getSelectedOption("#xy"),
                    "zy" to doc.getSelectedOption("#zy"),
                    "kb" to doc.getSelectedOption("#kb"),
            )
            if (!doc.checkSelectedOption("#xq", SemesterInfoProperty.SEMESTER.toString(10))){
                throw ServerRuntimeException("无法选中目标学期")
            }
            Thread.sleep(delay)
        }

        // 龑江学区教室合集
        val rooms = HashSet<ClassRoom>()
        facultyChart.deleteAllInBatch()
        specialtyChart.deleteAllInBatch()
        classChart.deleteAllInBatch()

        // 按学年依次选中
        doc.select("#nj")[0].childNodes().forEach grade@{ gradeNode ->
            val grade = gradeNode.attr("value").toShortOrNull() ?: return@grade

            val selectedGrade: ViewStateDocument
            if (!doc.checkSelectedOption("#nj", grade.toString(10))){
                selectedGrade = doc.post(
                        "__EVENTTARGET" to "xq",
                        "xn" to SemesterInfoProperty.YEAR,
                        "xq" to SemesterInfoProperty.SEMESTER,
                        "nj" to grade,
                        "xy" to doc.getSelectedOption("#xy"),
                        "zy" to doc.getSelectedOption("#zy"),
                        "kb" to doc.getSelectedOption("#kb"),
                )
                Thread.sleep(delay)
                if (!selectedGrade.checkSelectedOption("#xq", SemesterInfoProperty.SEMESTER.toString(10))){
                    throw ServerRuntimeException("无法选中目标学期")
                }
            } else {
                selectedGrade = doc
            }

            // 按学院依次选中
            selectedGrade.select("#xy")[0].childNodes().forEach faculty@{ facultyNode ->
                val faculty = facultyNode.attr("value").toIntOrNull() ?: return@faculty
                val facultyName: String = Jsoup.parse(facultyNode.outerHtml()).text()

                val selectedFaculty: ViewStateDocument
                if (!selectedGrade.checkSelectedOption("#xy", faculty.toString(10))) {
                    selectedFaculty = doc.post(
                            "__EVENTTARGET" to "zy",
                            "xn" to SemesterInfoProperty.YEAR,
                            "xq" to SemesterInfoProperty.SEMESTER,
                            "nj" to grade,
                            "xy" to faculty,
                            "zy" to doc.getSelectedOption("#zy"),
                            "kb" to doc.getSelectedOption("#kb"),
                    )
                    Thread.sleep(delay)
                    if (!selectedFaculty.checkSelectedOption("#xy", faculty.toString(10))) {
                        throw ServerRuntimeException("无法选中目标学院")
                    }
                } else {
                    selectedFaculty = selectedGrade
                }

                // 按专业依次选中
                selectedFaculty.select("#zy")[0].childNodes().forEach specialty@{ specialtyNode ->
                    val specialty = specialtyNode.attr("value").toIntOrNull() ?: return@specialty
                    val specialtyName: String = Jsoup.parse(specialtyNode.outerHtml()).text()

                    val selectedSpecialty: ViewStateDocument
                    if (!selectedFaculty.checkSelectedOption("#zy", specialty.toString(10))) {
                        selectedSpecialty = selectedFaculty.post(
                                "__EVENTTARGET" to "zy",
                                "xn" to SemesterInfoProperty.YEAR,
                                "xq" to SemesterInfoProperty.SEMESTER,
                                "nj" to grade,
                                "xy" to faculty,
                                "zy" to specialty,
                                "kb" to doc.getSelectedOption("#kb"),
                        )
                        Thread.sleep(delay)
                        if (!selectedSpecialty.checkSelectedOption("#zy", specialty.toString(10))) {
                            throw ServerRuntimeException("无法选中目标专业")
                        }
                    } else {
                        selectedSpecialty = selectedFaculty
                    }

                    // 按课表依次选中
                    selectedSpecialty.select("#kb")[0].childNodes().forEach clazz@{ classNode ->
                        val tableId = classNode.attr("value").takeIf {
                            StringUtils.hasText(it)
                        } ?: return@clazz
                        val className: String = Jsoup.parse(classNode.outerHtml()).text()
                        val classId = classIdCheck.matcher(className).let classId@{
                            if (!it.find()) throw ServerRuntimeException("班级ID获取失败")
                            return@classId it.group(0).replace("班", "").toShortOrNull()
                                    ?: throw ServerRuntimeException("班级ID解析失败")
                        }
                        classChart.save(ClassChart().also {
                            it.specialty = specialty
                            it.faculty = faculty
                            it.name = className
                            it.grade = grade
                            it.classId = classId
                        })
                        facultyChart.save(FacultyChart().also {
                            it.name = facultyName
                            it.faculty = faculty
                        })
                        specialtyChart.save(SpecialtyChart().also {
                            it.specialty = specialty
                            it.name = specialtyName
                            it.faculty = faculty
                        })

                        val exist = schedule.getById(tableId)
                        if (!exist.isExpired()) {
                            var count = 0
                            Log.d("课表未过期，取消刷新：$className")
                            for (day in exist.getContent()) {
                                for (classes in day) {
                                    for (clazz in classes) {
                                        count++
                                        if (!roomCheck.matcher(clazz.room).find()) {
                                            continue
                                        }
                                        rooms.add(ClassRoom().also {
                                            it.id = rooms.size
                                            it.name = clazz.room
                                        })
                                    }
                                }
                            }
                            if (count == 0) {
                                Log.d("删除空白课表：$className")
                                schedule.deleteById(tableId)
                                result = false
                            }
                            return@clazz
                        }

                        val selectedClass = selectedSpecialty.takeIf {
                            selectedSpecialty.checkSelectedOption("#kb", tableId)
                        } ?: selectedSpecialty.post(
                            "__EVENTTARGET" to "kb",
                            "xn" to SemesterInfoProperty.YEAR,
                            "xq" to SemesterInfoProperty.SEMESTER,
                            "nj" to grade,
                            "xy" to faculty,
                            "zy" to specialty,
                            "kb" to tableId,
                        )
                        Thread.sleep(delay)

                        val data = parseSchedule(selectedClass, rooms)
                        if (data == null) {
                            Log.w("课表数据获取为空：$className")
                            result = false
                            return@clazz
                        }
                        Log.d("保存课表：$className")
                        schedule.save(ClassSchedule().also {
                            it.id = tableId
                            it.faculty = faculty
                            it.specialty = specialty
                            it.classId = classId
                            it.grade = grade
                            it.year = SemesterInfoProperty.YEAR
                            it.semester = SemesterInfoProperty.SEMESTER
                            it.content = data.toString()
                        })
                    }
                }
            }
        }
        facultyChart.flush()
        specialtyChart.flush()
        classChart.flush()
        schedule.flush()
        if (result && rooms.isNotEmpty()) {
            room.deleteAllInBatch()
            room.saveAllAndFlush(rooms)
        }
        return result
    }

    /**
     * 获取教师日程表
     */
    @Suppress("UNUSED_PARAMETER")
    private fun refreshTeacher(user: UserInfo, session: String): Boolean {
        throw ServiceUnavailableException()
    }

    /** 解析课表数据 */
    fun parseSchedule(doc: ViewStateDocument, rooms: HashSet<ClassRoom>): ScheduleData? {
        val result = ScheduleData()
        var resultCount = 0
        val classInfoPattern = Pattern.compile("<font color=\"red\">(.*?)</font>")
        val trs = doc.getElementById("Table6")!!
            .getElementsByTag("tbody")
            .select("tr")
        trs.forEachIndexed trFor@{ trIndex, tr ->
            if (trIndex % 2 == 1 || trIndex == 0) return@trFor
            val tds = tr.select("td")
            val tdIndexVar = tds.size - 8
            tds.forEachIndexed tdFor@{ tdIndex, td ->
                val classInfo = td.html().replace("\n", "")
                if (!classInfo.contains("<br>")) return@tdFor
                val classes = classInfoPattern.matcher(classInfo)
                    .replaceAll("")
                    .split("<br><br><br>")
                classes.forEach { content ->
                    val classEntry = when(tdIndex - tdIndexVar) {
                        1 -> result.monday
                        2 -> result.tuesday
                        3 -> result.wednesday
                        4 -> result.thursday
                        5 -> result.friday
                        6 -> result.saturday
                        7 -> result.sunday
                        else -> return@tdFor
                    }.run {
                        return@run when(trIndex){
                            2 -> am1
                            4 -> am2
                            6 -> pm1
                            8 -> pm2
                            10 -> ev
                            else -> return@trFor
                        }
                    }
                    val item = ScheduleData.ScheduleItem().apply {
                        val singleData = content.split("<br>")
                        this.name = singleData[0]
                        val stringClass = singleData[1].run {
                            return@run substring(0, indexOf("("))
                        }
                        if (stringClass.contains(",")) {
                            stringClass.split(",")
                        } else {
                            listOf(stringClass)
                        }.forEach { ranges ->
                            val localRange = if (ranges.contains("-")){
                                ranges.replace("单", "")
                                    .replace("双", "")
                                    .split("-")
                            } else {
                                listOf(ranges, ranges)
                            }
                            for (rangeIndex in localRange[0].toShort() .. localRange[1].toShort()){
                                val index = rangeIndex % 2 == 0
                                if ((ranges.contains("双") && !index)
                                    || (ranges.contains("单") && index)){
                                    continue
                                }
                                this.range.add(rangeIndex.toShort())
                            }
                        }
                        this.teacher = singleData[2]
                        this.room = singleData[3]
                        if (roomCheck.matcher(this.room).find()) {
                            rooms.add(ClassRoom().also {
                                it.id = rooms.size
                                it.name = this.room
                            })
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

        return result.takeIf { resultCount > 0 }
    }

    companion object {
        /** 匹配龑江学村教室名称 */
        private val roomCheck = Pattern.compile("[A-Z][0-9]+")
        /** 匹配学生班级 ID */
        private val classIdCheck = Pattern.compile("(\\d+)\\.?(\\d+)班")

        /** 高并发锁 */
        private val lock: Any = Object()
        /** 学生课表下一次更新时间，初始值为 -1，代表启动时刷新一次 */
        private var studentUpdate: Long = -1
        /** 教师日程表下一次更新时间，初始值为 -1，代表启动时刷新一次 */
        private var teacherUpdate: Long = -1
        /** 刷新间隔 1 天 */
        @Suppress("PrivatePropertyName")
        private val REFRESH_DELAY: Int = 60 * 60 * 24
        /** 刷新重试间隔 1 小时 */
        @Suppress("PrivatePropertyName")
        private val RETRY_DELAY: Int = 20.takeIf { Application.DEBUG } ?: (60 * 20)
    }
}