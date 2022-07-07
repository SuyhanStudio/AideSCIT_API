package io.github.sgpublic.aidescit.api

import io.github.sgpublic.aidescit.api.controller.LoginController
import io.github.sgpublic.aidescit.api.core.spring.property.KeyProperty
import io.github.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import io.github.sgpublic.aidescit.api.core.util.RSAUtil
import io.github.sgpublic.aidescit.api.data.ScheduleData
import io.github.sgpublic.aidescit.api.data.ViewStateDocument
import io.github.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import io.github.sgpublic.aidescit.api.mariadb.domain.ClassRoom
import io.github.sgpublic.aidescit.api.module.APIModule
import io.github.sgpublic.aidescit.api.module.ScheduleModule
import io.github.sgpublic.aidescit.api.module.SessionModule
import io.github.sgpublic.aidescit.api.module.UserInfoModule
import org.jsoup.Jsoup
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.StringUtils
import java.util.*
import java.util.regex.Pattern
import javax.crypto.Cipher

/**
 * @Author sgpublic
 * @Date 2022/4/3 10:37
 * @Description
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmptyClassRoomFinder {
    @Autowired
    private lateinit var login: LoginController
    @Autowired
    private lateinit var schedule: ScheduleModule
    @Autowired
    private lateinit var info: UserInfoModule
    @Autowired
    private lateinit var session: SessionModule

    private lateinit var username: String
    private lateinit var password: String

    val pub: Cipher get() {
        val cp = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cp.init(Cipher.ENCRYPT_MODE, KeyProperty.PUBLIC_KEY)
        return cp
    }

    @BeforeAll
    fun initAccount() {
        val input = Scanner(System.`in`)
        print("请输入学号：")
        username = input.next()
        print("请输入密码：")
        password = input.next()
    }

    @Test
    fun start() {
        val access = login.login(
            username, RSAUtil.encode(password, pub), ""
        )["access_token"]
        requireNotNull(access) { "登录失败" }

        val user = info.get(username)
        val session = session.get(username)

        val url = "http://218.6.163.93:8081/tjkbcx.aspx?xh=${user.username}"
        var doc = APIModule.executeDocument(
            url = url,
            headers = APIModule.buildHeaders(
                "Referer" to url
            ),
            cookies = session.getCookie(),
            method = APIModule.METHOD_GET
        )

        val result = LinkedList<Pair<ScheduleData.ScheduleItem, String>>()
        val roomCheck = Pattern.compile("[A-Z][0-9]+")
        val rooms = HashSet<ClassRoom>()

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
        }

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
        }

        doc.select("#nj")[0].childNodes().forEach grade@{ gradeNode ->
            val grade = gradeNode.attr("value").toIntOrNull() ?: return@grade

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
                if (!selectedGrade.checkSelectedOption("#xq", SemesterInfoProperty.SEMESTER.toString(10))){
                    throw ServerRuntimeException("无法选中目标学期")
                }
            } else {
                selectedGrade = doc
            }

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
                    if (!selectedFaculty.checkSelectedOption("#xy", faculty.toString(10))) {
                        throw ServerRuntimeException("无法选中目标学院")
                    }
                } else {
                    selectedFaculty = selectedGrade
                }

                selectedFaculty.select("#zy")[0].childNodes().forEach specialty@{ specialtyNode ->
                    val specialty = specialtyNode.attr("value").toIntOrNull() ?: return@specialty
                    val specialtyName: String = facultyName + " " + Jsoup.parse(specialtyNode.outerHtml()).text()

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
                        if (!selectedSpecialty.checkSelectedOption("#zy", specialty.toString(10))) {
                            throw ServerRuntimeException("无法选中目标专业")
                        }
                    } else {
                        selectedSpecialty = selectedFaculty
                    }

                    selectedSpecialty.select("#kb")[0].childNodes().forEach clazz@{ classNode ->
                        val tableId = classNode.attr("value").takeIf {
                            StringUtils.hasText(it)
                        } ?: return@clazz
                        val className: String = specialtyName + " " + Jsoup.parse(classNode.outerHtml()).text()

                        val selectedClass = selectedSpecialty.post(
                            "__EVENTTARGET" to "kb",
                            "xn" to SemesterInfoProperty.YEAR,
                            "xq" to SemesterInfoProperty.SEMESTER,
                            "nj" to grade,
                            "xy" to faculty,
                            "zy" to specialty,
                            "kb" to tableId,
                        )
                        println("解析：$className")
                        schedule.parseSchedule(selectedClass, rooms)?.let {
                            for (clazz in it.saturday.am1) {
                                result.add(Pair(clazz, className))
                            }
                        }
                    }
                }
            }
        }

        println("-------------------------可用教室-------------------------")
        for (room in rooms) {
            println(room)
        }

        println("-------------------------课程-------------------------")
        for ((clazz, name) in result) {
            rooms.remove(ClassRoom().also {
                it.name = clazz.room
            })
            println("$clazz $name")
        }

        println("-------------------------空闲教室-------------------------")
        for (room in rooms) {
            println(room.name)
        }
    }
}