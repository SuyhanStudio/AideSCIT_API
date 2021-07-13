package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import com.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import com.sgpublic.aidescit.api.mariadb.dao.UserInfoRepository
import com.sgpublic.aidescit.api.mariadb.domain.UserInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class UserInfoModule {
    @Autowired
    private lateinit var info: UserInfoRepository
    @Autowired
    private lateinit var session: SessionModule

    fun get(username: String): UserInfo {
        val userInfo = info.getByUsername(username)
            ?: return refresh(username)
        return if (userInfo.isExpired()){
            refresh(username)
        } else {
            userInfo
        }
    }

    private fun refresh(username: String): UserInfo {
        val result = UserInfo()
        val session = session.get(username).session
        val url1 = "http://218.6.163.93:8081/xsgrxx.aspx?xh=$username"
        val doc1 = APIModule.executeDocument(
            url = url1,
            cookies = APIModule.buildCookies(
                APIModule.COOKIE_KEY to session
            ),
            headers = APIModule.buildHeaders(
                "Referer" to url1
            ),
            method = APIModule.METHOD_GET
        ).document
        result.grade = doc1.select("#lbl_dqszj").text().run {
            if (this == ""){
                throw ServerRuntimeException("年级获取失败")
            }
            return@run toIntOrNull() ?: throw ServerRuntimeException("年级ID解析失败")
        }
        result.name = doc1.select("#xm").text().run {
            if (this == ""){
                throw ServerRuntimeException("姓名获取失败")
            }
            return@run this
        }
        result.classId = doc1.select("#lbl_xzb").text().run {
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
        val lblXy = doc1.select("#lbl_xy").text().run {
            if (this == ""){
                throw ServerRuntimeException("学院名称获取失败")
            }
            return@run this
        }
        val lblZymc = doc1.select("#lbl_zymc").text().run {
            if (this == ""){
                throw ServerRuntimeException("专业名称获取失败")
            }
            return@run this
        }
        val url2 = "http://218.6.163.93:8081/tjkbcx.aspx?xh=$username"
        val doc2 = APIModule.executeDocument(
            url = url2,
            cookies = APIModule.buildCookies(
                APIModule.COOKIE_KEY to session
            ),
            headers = APIModule.buildHeaders(
                "Referer" to url2
            ),
            method = APIModule.METHOD_GET
        )
        result.faculty = doc2.document.select("#xy").run {
            forEach { element ->
                if (element.text() == lblXy){
                    return@run element.attr("value").toIntOrNull()
                        ?: throw ServerRuntimeException("学院ID解析失败")
                }
            }
            throw ServerRuntimeException("学院ID获取失败")
        }
        val yearStart = SemesterInfoProperty.YEAR.split("-")[0].toInt()
        var viewstate = doc2.viewstate
        for (i in 0 until -6){
            val year = "${yearStart + i}-${yearStart + i + 1}"
            val doc3 = APIModule.executeDocument(
                url = url2,
                headers = APIModule.buildHeaders(
                    "Referer" to url2
                ),
                cookies = APIModule.buildCookies(
                    APIModule.COOKIE_KEY to session
                ),
                body = APIModule.buildFormBody(
                    "__EVENTTARGET" to "xq",
                    "__EVENTARGUMENT" to "",
                    "__LASTFOCUS" to "",
                    "__VIEWSTATE" to viewstate,
                    "__VIEWSTATEGENERATOR" to "3189F21D",
                    "xn" to year,
                    "xq" to 1,
                    "nj" to result.grade,
                    "xy" to result.faculty,
                ),
                method = APIModule.METHOD_POST
            )
            viewstate = doc3.viewstate
            result.specialty = doc3.document.select("#zy").select("option").run {
                forEach { element ->
                    if (element.text() == lblZymc){
                        return@run element.attr("value").toIntOrNull()
                            ?: throw ServerRuntimeException("专业ID解析失败")
                    }
                }
                throw ServerRuntimeException("专业ID获取失败")
            }
        }
        info.save(result)
        return result
    }
}