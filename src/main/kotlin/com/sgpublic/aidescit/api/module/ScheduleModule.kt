package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import com.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import com.sgpublic.aidescit.api.exceptions.ServiceUnavailableException
import com.sgpublic.aidescit.api.mariadb.dao.ClassScheduleRepository
import com.sgpublic.aidescit.api.mariadb.domain.UserInfo
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ScheduleModule {
    @Autowired
    private lateinit var schedule: ClassScheduleRepository
    @Autowired
    private lateinit var info: UserInfoModule
    @Autowired
    private lateinit var session: SessionModule

    fun get(username: String, year: String = SemesterInfoProperty.YEAR,
            semester: Short = SemesterInfoProperty.SEMESTER): JSONObject {
        val userInfo = info.get(username)
        val data = schedule.getSchedule(userInfo.faculty, userInfo.specialty, userInfo.classId, year, semester)
            ?: return refresh(username, year, semester)
        return if (data.isExpired()){
            refresh(username, year, semester)
        } else {
            data.getContent()
        }
    }

    private fun refresh(username: String, year: String, semester: Short): JSONObject {
        val user = info.get(username)
        val session = session.get(username).session
        return when (user.identify){
            0 -> {
                refreshStudent(user, year, semester, session)
            }
            1 -> {
                refreshTeacher(user, year, semester, session)
            }
            else -> throw ServerRuntimeException.INTERNAL_ERROR
        }
    }

    private fun refreshStudent(user: UserInfo, year: String, semester: Short, session: String): JSONObject {
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

        val doc1 = APIModule.executeDocument(
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
            method = APIModule.METHOD_GET
        )
        viewstate = doc1.viewstate
        TODO("待完善逻辑")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun refreshTeacher(user: UserInfo, year: String, semester: Short, session: String): JSONObject {
        throw ServiceUnavailableException()
    }
}