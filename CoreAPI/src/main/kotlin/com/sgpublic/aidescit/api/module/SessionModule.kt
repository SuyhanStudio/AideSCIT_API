package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.util.Log
import com.sgpublic.aidescit.api.core.util.RSAUtil
import com.sgpublic.aidescit.api.exceptions.InvalidPasswordFormatException
import com.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import com.sgpublic.aidescit.api.exceptions.UserNotFoundException
import com.sgpublic.aidescit.api.exceptions.WrongPasswordException
import com.sgpublic.aidescit.api.mariadb.dao.UserSessionRepository
import com.sgpublic.aidescit.api.mariadb.domain.UserSession
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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
        if (userSession.existsById(username)){
            val result = userSession.getUserSession(username)
                ?: return refresh(username, password)
            if (result.isEffective() && !result.isExpired()){
                return result
            }
        }
        return refresh(username, password)
    }

    /**
     * 从教务系统获取新的 ASP.NET_SessionId
     * @param username 用户学号/工号
     * @param password 用户明文密码
     * @return 返回 [UserSession]
     */
    private fun refresh(username: String, password: String?): UserSession {
        Log.d("刷新 ASP.NET_SessionId", username)
        val pwd: String = password
            ?: userSession.getUserPassword(username)
            ?: throw UserNotFoundException()
        val passwordDecrypted = RSAUtil.decode(pwd).apply {
            if (length <= 8){
                throw InvalidPasswordFormatException()
            }
        }.substring(8)
        val result = getVerifyLocation(username, passwordDecrypted)
        val resp = APIModule.executeResponse(
            url = result.verifyLocation,
            method = APIModule.METHOD_GET
        )
        result.session = resp.headers["Set-Cookie"].run {
            if (this == null){
                throw ServerRuntimeException("ASP.NET_SessionId 获取失败")
            }
            if (length <= 50){
                throw ServerRuntimeException("ASP.NET_SessionId 解析失败")
            }
            return@run substring(18, length - 32)
        }

        result.id = username
        result.password = pwd
        userSession.save(result)
        return result
    }

    /**
     * 从教务系统获取最后一步跳转链接
     * @param username 用户学号/工号
     * @param password 用户明文密码
     * @return 返回 [UserSession]
     */
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
        val pwd: String = password
            ?: userSession.getUserPassword(username)
            ?: throw UserNotFoundException()
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
        val location5 = headers4["Location"] ?: throw ServerRuntimeException("第二次跳转失败")
        result.verifyLocation = location5
        return result
    }
}