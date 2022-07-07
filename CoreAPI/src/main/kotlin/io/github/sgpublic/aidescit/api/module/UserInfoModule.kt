package io.github.sgpublic.aidescit.api.module

import io.github.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import io.github.sgpublic.aidescit.api.core.util.Log
import io.github.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import io.github.sgpublic.aidescit.api.mariadb.dao.ClassChartRepository
import io.github.sgpublic.aidescit.api.mariadb.dao.FacultyChartRepository
import io.github.sgpublic.aidescit.api.mariadb.dao.SpecialtyChartRepository
import io.github.sgpublic.aidescit.api.mariadb.dao.UserInfoRepository
import io.github.sgpublic.aidescit.api.mariadb.domain.ClassChart
import io.github.sgpublic.aidescit.api.mariadb.domain.FacultyChart
import io.github.sgpublic.aidescit.api.mariadb.domain.SpecialtyChart
import io.github.sgpublic.aidescit.api.mariadb.domain.UserInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * 用户基本信息模块
 */
@Component
class UserInfoModule {
    @Autowired
    private lateinit var info: UserInfoRepository
    @Autowired
    private lateinit var classChart: ClassChartRepository
    @Autowired
    private lateinit var facultyChart: FacultyChartRepository
    @Autowired
    private lateinit var specialtyChart: SpecialtyChartRepository
    @Autowired
    private lateinit var session: SessionModule

    /**
     * 获取用户基本信息
     * @param username 用户学号/工号
     */
    fun get(username: String): UserInfo {
        val userInfo = info.getByUsername(username)
            ?: return refresh(username)
        return if (userInfo.isExpired()){
            refresh(username)
        } else {
            userInfo
        }
    }

    /**
     * 从教务系统刷新用户基本信息
     * @param username 用户学号/工号
     */
    private fun refresh(username: String): UserInfo {
        Log.d("刷新用户信息", username)
        val result = UserInfo()
        result.username = username
        val session = session.get(username).also {
            result.identify = it.identify
        }
        val url1 = "http://218.6.163.93:8081/xsgrxx.aspx?xh=$username"
        var doc = APIModule.executeDocument(
            url = url1,
            headers = APIModule.buildHeaders(
                APIModule.REFERER to url1
            ),
            cookies = session.getCookie(),
            method = APIModule.METHOD_GET
        )

        result.grade = doc.select("#lbl_dqszj").text().run {
            if (this == ""){
                throw ServerRuntimeException("年级获取失败")
            }
            return@run toShortOrNull() ?: throw ServerRuntimeException("年级ID解析失败")
        }
        result.name = doc.select("#xm").text().run {
            if (this == ""){
                throw ServerRuntimeException("姓名获取失败")
            }
            return@run this
        }
        val lblXzb = doc.select("#lbl_xzb").text().run {
            if (this == ""){
                throw ServerRuntimeException("班级名称获取失败")
            }
            return@run this
        }
        result.classId = lblXzb.run {
            if (this == ""){
                throw ServerRuntimeException("班级名称获取失败")
            }
            val match = Pattern.compile("(\\d+)\\.?(\\d+)班").matcher(this)
            if (match.find()){
                return@run match.group(0)
                    .replace("班", "").toShortOrNull()
                    ?: throw ServerRuntimeException("班级ID解析失败")
            } else {
                throw ServerRuntimeException("班级ID获取失败")
            }
        }
        val lblXy = doc.select("#lbl_xy").text().run {
            if (this == ""){
                throw ServerRuntimeException("学院名称获取失败")
            }
            return@run this
        }

        val lblZymc = doc.select("#lbl_zymc").text().run {
            if (this == ""){
                throw ServerRuntimeException("专业名称获取失败")
            }
            return@run this
        }
        doc = doc.get("http://218.6.163.93:8081/tjkbcx.aspx?xh=$username")
        result.faculty = doc.select("#xy").select("option").run {
            forEach { element ->
                if (element.text() == lblXy){
                    return@run element.attr("value").toIntOrNull()
                        ?: throw ServerRuntimeException("学院ID解析失败")
                }
            }
            throw ServerRuntimeException("学院ID获取失败：$lblXy")
        }
        val yearStart = SemesterInfoProperty.YEAR.split("-")[0].toInt()
        for (i in 0 until 6){
            val year = "${yearStart - i}-${yearStart - i + 1}"
            doc = doc.post(
                "__EVENTTARGET" to "xq",
                "xn" to year,
                "xq" to 1,
                "nj" to result.grade,
                "xy" to result.faculty,
            )
            doc.select("#zy").select("option").forEach { element ->
                if (element.text() != lblZymc){
                    return@forEach
                }
                result.specialty = element.attr("value").toIntOrNull()
                    ?: throw ServerRuntimeException("专业ID解析失败")
                specialtyChart.save(SpecialtyChart().apply {
                    specialty = result.specialty
                    name = lblZymc
                    faculty = result.faculty
                })
                facultyChart.save(FacultyChart().apply {
                    name = lblXy
                    faculty = result.faculty
                })
                classChart.save(ClassChart().apply {
                    specialty = result.specialty
                    faculty = result.faculty
                    name = lblXzb
                    grade = result.grade
                    classId = result.classId
                })
                info.save(result)
                return result
            }
        }
        throw ServerRuntimeException("专业ID获取失败：$lblZymc")
    }
}