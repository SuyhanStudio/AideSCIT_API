package io.github.sgpublic.aidescit.api.module

import io.github.sgpublic.aidescit.api.core.util.Log
import io.github.sgpublic.aidescit.api.core.util.RSAUtil
import io.github.sgpublic.aidescit.api.core.util.advMapOf
import io.github.sgpublic.aidescit.api.core.util.jsonBody
import io.github.sgpublic.aidescit.api.exceptions.InvalidPasswordFormatException
import io.github.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import io.github.sgpublic.aidescit.api.exceptions.UserNotFoundException
import io.github.sgpublic.aidescit.api.exceptions.WrongPasswordException
import io.github.sgpublic.aidescit.api.mariadb.dao.UserSessionRepository
import io.github.sgpublic.aidescit.api.mariadb.domain.UserSession
import okhttp3.internal.closeQuietly
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * 用于获取和刷新用户 ASP.NET_SessionId
 */
@Component
class SessionModule {
    @Autowired
    private lateinit var userSession: UserSessionRepository

    /**
     * 获取用户 session
     * @param username 用户学号/工号
     * @param password 用户加盐密文密码，若传入 null 则从数据库调取已有数据
     * @return 返回 [UserSession]
     * @throws UserNotFoundException 参数 [password] 传入 null 但该用户并未注册时抛出
     * @throws InvalidPasswordFormatException 参数 [password] 未加盐时抛出
     */
    fun get(username: String, password: String? = null): UserSession {
        return userSession.getUserSession(username)?.takeIf {
            it.isEffective() && !it.isExpired() && checkSession(username, it)
        } ?: refreshSession(username, password)
    }

    /**
     * 检查 ASP.NET_SessionId 是否有效
     * @param username 用户学号/工号
     * @param session UserSession
     */
    private fun checkSession(username: String, session: UserSession): Boolean {
        return try {
            val url = "http://218.6.163.93:8081/xs_main.aspx?xh=$username"
            APIModule.executeDocument(
                url = url,
                headers = APIModule.buildHeaders(
                    "Referer" to url
                ),
                cookies = session.getCookie(),
                method = APIModule.METHOD_GET
            ).select("#icode").isEmpty()
        } catch (e: ServerRuntimeException){ false }
    }

    private final val route: Pattern = Pattern.compile("route=[a-z0-9]{32}")
    private final val session: Pattern = Pattern.compile("ASP.NET_SessionId=[a-z0-9]{24}")
    /**
     * 从教务系统获取新的登录令牌
     * @param username 用户学号/工号
     * @param password 用户加盐密文密码，若传入 null 则从数据库调取已有数据
     * @return 返回 [UserSession]
     */
    private fun refreshSession(username: String, password: String?): UserSession {
        Log.d("刷新 ASP.NET_SessionId", username)
        val pwd: String = password
            ?: userSession.getUserPassword(username)
            ?: throw UserNotFoundException()
        val passwordDecrypted = RSAUtil.decode(pwd).apply {
            if (length <= 8){
                throw InvalidPasswordFormatException()
            }
        }.substring(8)
        val result = getSpringboardLocation(username, passwordDecrypted)
        val resp1 = APIModule.executeResponse(
            url = result.verifyLocation,
            method = APIModule.METHOD_GET
        )
        resp1.headers.toMultimap()["Set-Cookie"].let {
            if (it == null || it.size != 2){
                throw ServerRuntimeException("Cookie 获取失败")
            }
            val route = this.route.matcher(it[0])
            if (!route.find()){
                throw ServerRuntimeException("Cookie: route 解析失败")
            }
            result.route = route.group().split("=")[1]
            val session = this.session.matcher(it[1])
            if (!session.find()){
                throw ServerRuntimeException("Cookie: ASP.NET_SessionId 解析失败")
            }
            result.session = session.group().split("=")[1]
        }
        val location1 = resp1.header("Location")
            ?: throw ServerRuntimeException("第一次跳转失败")
        resp1.close()

        APIModule.executeResponse(
            url = "http://218.6.163.93:8081$location1",
            method = APIModule.METHOD_GET,
            cookies = result.getCookie()
        ).closeQuietly()

        result.id = username
        result.password = pwd
        userSession.save(result)
        return result
    }

    /** 办事大厅返回数据序列化 */
    private val respClass = object {
        var code: String = ""
        val content = object {
            var ticket: String = ""
            var token: String = ""
            var redirectUrl: String = ""
        }
    }.javaClass
    /**
     * 从办事大厅获取教务系统跳转链接
     * @param username 用户学号/工号
     * @param password 用户明文密码，若传入 null 则从数据库调取已有数据
     * @return 返回 [UserSession]
     */
    fun getSpringboardLocation(username: String, password: String? = null): UserSession {
        val result = UserSession()

        val param1 = advMapOf(
            "universityId" to 100831,
            "appKey" to "uap-web-key",
            "timestamp" to APIModule.TS_FULL,
            "nonce" to APIModule.NONCE,
            "clientCategory" to "PC",
            "appCode" to "officeHallApplicationCode",
            "equipmentName" to "工科助手(sgpublic2002@gmail.com)",
            "equipmentId" to "SpringBoot",
            "equipmentVersion" to "1.0.0-alpha01",
            "accountNumber" to username,
            "loginWay" to "ACCOUNT"
        )
        APIModule.getSecretParam(param1)
        val resp1 = APIModule.executeResponse(
            url = "http://ai.scit.cn/ump/common/login/getLoginTicket",
            method = APIModule.METHOD_POST,
            body = APIModule.buildFormBody(param1)
        )
        val loginTicket: String = resp1.jsonBody(respClass).let {
            if (it.code != "40001") {
                Log.d(this)
                throw ServerRuntimeException.NETWORK_FAILED
            }
            return@let it.content.ticket
        }

        val pwd: String = password ?: userSession.getUserPassword(username)?.run {
            return@run RSAUtil.decode(this).apply {
                if (length <= 8){
                    throw InvalidPasswordFormatException()
                }
            }.substring(8)
        } ?: throw UserNotFoundException()
        val param2 = advMapOf(
            "universityId" to 100831,
            "password" to pwd,
            "appKey" to "uap-web-key",
            "timestamp" to APIModule.TS_FULL,
            "nonce" to APIModule.NONCE,
            "clientCategory" to "PC",
        )
        APIModule.getSecretParam(param2)
        param2.remove("password")
        val resp2 = APIModule.executeResponse(
            url = "http://ai.scit.cn/ump/pc/login/account/checkAccount/$loginTicket",
            method = APIModule.METHOD_POST,
            body = APIModule.buildFormBody(param2)
        )
        val resp2json = resp2.jsonBody(respClass)
        if (resp2json.code == "20003"){
            throw WrongPasswordException(username)
        }
        if (resp2json.code != "40001"){
            Log.d(resp2json)
            throw ServerRuntimeException.NETWORK_FAILED
        }
        result.cookie = resp2.header(
            "Set-Cookie", ""
        )!!.run {
            substring(28, length - 61)
        }

        val param3 = advMapOf(
            "universityId" to 100831,
            "appKey" to "uap-web-key",
            "timestamp" to APIModule.TS_FULL,
            "nonce" to APIModule.NONCE,
            "clientCategory" to "PC",
        )
        APIModule.getSecretParam(param3)
        val resp3 = APIModule.executeResponse(
            url = "http://ai.scit.cn/ump/pc/login/uap/sso/$loginTicket",
            method = APIModule.METHOD_POST,
            body = APIModule.buildFormBody(param3),
            cookies = APIModule.buildCookies(
                "AUTHENTICATION_NORMAL_LOGIN" to result.cookie
            )
        ).jsonBody(respClass)
        if (resp3.code != "40001"){
            Log.d(resp3)
            throw ServerRuntimeException.NETWORK_FAILED
        }
        val location = resp3.content.redirectUrl
        val hallTicket = location.substring(56, location.length - 11)

        val resp4 = APIModule.executeResponse(
            url = "http://ai.scit.cn/ump/officeHallPageHome/uap/check/$hallTicket",
            method = APIModule.METHOD_GET,
            body = APIModule.buildFormBody(
                "universityId" to 100831,
                "appKey" to "pc-officeHall",
                "timestamp" to APIModule.TS_FULL,
                "nonce" to APIModule.NONCE,
                "equipmentName" to "工科助手(sgpublic2002@gmail.com)",
                "clientCategory" to "PC"
            ),
            cookies = APIModule.buildCookies(
                "AUTHENTICATION_NORMAL_LOGIN" to result.cookie,
                "userInfo" to advMapOf(
                    "operatorId" to "YK0000010000200003",
                    "userType" to "STUDENT",
                    "userId" to null
                ).toString()
            )
        ).jsonBody(respClass)
        if (resp4.code != "40001"){
            Log.d(resp4)
            throw ServerRuntimeException.NETWORK_FAILED
        }
        val hallToken = resp4.content.token

        val resp5 = APIModule.executeResponse(
            url = "http://ai.scit.cn/ump/user/front/info/pc",
            method = APIModule.METHOD_POST,
            body = APIModule.buildJsonBody(
                "universityId" to 100831,
                "appKey" to "pc-officeHall",
                "timestamp" to APIModule.TS_FULL,
                "nonce" to APIModule.NONCE,
                "clientCategory" to "PC"
            ),
            headers = APIModule.buildHeaders(
                "token" to hallToken
            ),
            cookies = APIModule.buildCookies(
                "AUTHENTICATION_NORMAL_LOGIN" to result.cookie,
                "userInfo" to advMapOf(
                    "operatorId" to "YK0000010000200003",
                    "userType" to "STUDENT",
                    "userId" to null
                ).toString(),
                "ump_token_pc-officeHall" to hallToken
            )
        ).jsonBody(respClass)
        if (resp5.code != "40001") {
            Log.d(resp5)
            throw ServerRuntimeException.NETWORK_FAILED
        }

        val resp6 = APIModule.executeResponse(
            url = "http://ai.scit.cn/ump/officeHall/getApplicationUrl",
            method = APIModule.METHOD_GET,
            body = APIModule.buildFormBody(
                "universityId" to 100831,
                "appKey" to "pc-officeHall",
                "timestamp" to APIModule.TS_FULL,
                "nonce" to APIModule.NONCE,
                "clientCategory" to "PC",
                "applicationCode" to "oboR46",
                "userType" to "STUDENT"
            ),
            headers = APIModule.buildHeaders(
                "token" to hallToken
            ),
            cookies = APIModule.buildCookies(
                "AUTHENTICATION_NORMAL_LOGIN" to result.cookie,
                "userInfo" to advMapOf(
                    "operatorId" to "YK0000010000200003",
                    "userType" to "STUDENT",
                    "userId" to null
                ).toString(),
                "ump_token_pc-officeHall" to hallToken
            )
        ).jsonBody(respClass)
        if (resp6.code != "40001"){
            Log.d(resp6)
            throw ServerRuntimeException.NETWORK_FAILED
        }
        result.verifyLocation = resp6.content.redirectUrl

        return result
    }

    /**
     * 从教务系统获取最后一步跳转链接
     * @param username 用户学号/工号
     * @param password 用户加盐密文密码，若传入 null 则从数据库调取已有数据
     * @return 返回 [UserSession]
     */
    @Deprecated("旧系统已关闭", ReplaceWith(
        "getSpringboardLocation(username, password)",
        "io.github.sgpublic.aidescit.api.module.SessionModule"
    ))
    fun getVerifyLocation(username: String, password: String? = null): UserSession {
        val result = UserSession()
        val resp1 = APIModule.executeResponse(
            url = "http://218.6.163.95:18080/zfca/login",
            method = APIModule.METHOD_GET
        )
        val jsId1 = resp1.header("Set-Cookie", null).run {
            if (this == null){
                throw ServerRuntimeException("JSESSIONID1 获取失败")
            }
            if (length <= 23){
                throw ServerRuntimeException("JSESSIONID1 处理失败")
            }
            return@run substring(11, length - 12)
        }
        val element = Jsoup.parse(
            resp1.body?.string() ?: throw ServerRuntimeException.NETWORK_FAILED
        ).body()
        val input = element.select(".btn")
            .select("span")
            .select("input")
        if (input.attr("name") != "lt"){
            throw ServerRuntimeException("lt 获取失败")
        }
        if (!input.hasAttr("value")){
            throw ServerRuntimeException("lt 解析失败")
        }
        val lt = input.attr("value")
        val pwd: String = password ?: userSession.getUserPassword(username)?.run {
            return@run RSAUtil.decode(this).apply {
                if (length <= 8){
                    throw InvalidPasswordFormatException()
                }
            }.substring(8)
        } ?: throw UserNotFoundException()
        val resp2 = APIModule.executeResponse(
            url = "http://218.6.163.95:18080/zfca/login;jsessionid=$jsId1",
            body = APIModule.buildFormBody(
                "useValidateCode" to 0,
                "isremenberme" to 0,
                "ip" to 0,
                "username" to username,
                "password" to pwd,
                "losetime" to 30,
                "lt" to lt,
                "_eventId" to "submit",
                "submit1" to "+"
            ),
            method = APIModule.METHOD_POST
        )
        val headers1 = resp2.headers
        val castgc = headers1["Set-Cookie"].run {
            if (this == null){
                val body = resp2.body?.string()
                    ?: throw ServerRuntimeException.NETWORK_FAILED
                if (body.indexOf("JSP Error Page") < 0){
                    throw WrongPasswordException(username)
                } else {
                    throw ServerRuntimeException.INTERNAL_ERROR
                }
            }
            if (length <= 19){
                throw ServerRuntimeException("CASTGC 解析失败")
            }
            return@run substring(7, length - 12)
        }
        val location1 = headers1["Location"] ?: throw ServerRuntimeException("第一次跳转失败")
        val resp3 = APIModule.executeResponse(
            url = location1,
            method = APIModule.METHOD_GET
        )
        val headers2 = resp3.headers
        val jsId2 = headers2["Set-Cookie"].run {
            if (this == null){
                throw ServerRuntimeException("JSESSIONID2 获取失败")
            }
            if (length <= 19){
                throw ServerRuntimeException("JSESSIONID2 处理失败")
            }
            return@run substring(11, length - 8)
        }
        val location2 = headers2["Location"] ?: throw ServerRuntimeException("第二次跳转失败")
        resp3.close()
        val resp4 = APIModule.executeResponse(
            url = location2,
            cookies = APIModule.buildCookies(
                "JSESSIONID" to jsId1,
                "CASTGC" to castgc,
                "JSESSIONID" to jsId2
            ),
            method = APIModule.METHOD_GET
        )
        val headers3 = resp4.headers
        val location3 = headers3["Location"] ?: throw ServerRuntimeException("第三次跳转失败")
        resp4.close()
        val resp5 = APIModule.executeResponse(
            url = location3,
            cookies = APIModule.buildCookies(
                "JSESSIONID" to jsId2
            ),
            method = APIModule.METHOD_GET
        )
        val body = resp5.body?.string() ?: throw ServerRuntimeException.NETWORK_FAILED
        val location4 = StringBuilder("http://218.6.163.95:18080/zfca/login?yhlx=")
        when {
            body.indexOf("teacher") != -1 -> {
                location4.append("teacher")
                result.identify = 1
            }
            body.indexOf("student") != -1 -> {
                location4.append("student")
                result.identify = 0
            }
            else -> {
                throw ServerRuntimeException("identity 获取失败")
            }
        }
        location4.append("&login=0122579031373493708&url=xs_main.aspx")
        val resp6 = APIModule.executeResponse(
            url = location4.toString(),
            cookies = APIModule.buildCookies(
                "JSESSIONID" to jsId1,
                "CASTGC" to castgc,
                "JSESSIONID" to jsId2
            ),
            method = APIModule.METHOD_GET
        )
        val headers4 = resp6.headers
        resp6.close()
        val location5 = headers4["Location"] ?: throw ServerRuntimeException("第二次跳转失败")
        result.verifyLocation = location5
        return result
    }
}