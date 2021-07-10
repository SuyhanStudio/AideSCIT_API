package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.util.Log
import com.sgpublic.aidescit.api.core.util.RSAUtil
import com.sgpublic.aidescit.api.exceptions.InvalidPasswordFormatException
import com.sgpublic.aidescit.api.exceptions.UserNotFoundException
import com.sgpublic.aidescit.api.exceptions.WrongPasswordException
import com.sgpublic.aidescit.api.mariadb.dao.UserTokenRepository
import com.sgpublic.aidescit.api.mariadb.domain.UserSession
import okio.IOException
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SessionModule {
    @Autowired
    private lateinit var userToken: UserTokenRepository

    /**
     * 获取用户 session
     * @param username 用户学号/工号
     * @param password 用户加盐密文密码，若传入 null 则从数据库调取已有数据
     * @return 返回 [UserSession]
     * @throws UserNotFoundException 参数 [password] 传入 null 但该用户并未注册时抛出
     * @throws InvalidPasswordFormatException 参数 [password] 未加盐时抛出
     */
    fun get(username: String, password: String?): UserSession {
        var pwd: String? = password
        if (userToken.existsById(username)){
            val result = userToken.getUserSession(username)
            if (result.isEffective() && result.isExpired()){
                return result
            }
            if (pwd == null) {
                pwd = userToken.getUserPassword(username)
            }
        } else if (pwd == null){
            throw UserNotFoundException()
        }
        pwd = RSAUtil.decode(pwd).apply {
            if (length <= 8){
                throw InvalidPasswordFormatException()
            }
        }.substring(8)
        return refresh(username, pwd)
    }

    /**
     * 从教务系统获取新的 ASP.NET_SessionId
     * @param username 用户学号/工号
     * @param password 用户明文密码
     * @return 返回 [UserSession]
     */
    private fun refresh(username: String, password: String): UserSession {
        return getVerifyLocation(username, password)
    }

    /**
     * 从教务系统获取最后一步跳转链接
     * @param username 用户学号/工号
     * @param password 用户明文密码
     * @return 返回 [UserSession]
     */
    fun getVerifyLocation(username: String, password: String): UserSession {
        val result = UserSession()
        val resp1 = APIModule.buildRequest(
            url = "http://218.6.163.95:18080/zfca/login",
            method = APIModule.METHOD_GET
        ).execute()
        val jsId1 = resp1.header("Set-Cookie", null).run {
            if (this == null){
                throw IOException("JSESSIONID1 获取失败")
            }
            if (length <= 11){
                throw IOException("JSESSIONID1 处理失败")
            }
            return@run substring(11, length - 1)
        }
        val element = Jsoup.parse(
            resp1.body?.string() ?: throw IOException("网络请求失败")
        ).body()
        val input = element.select(".btn")
            .select("span")
            .select("input")
        if (input.attr("name") != "lt"){
            throw IOException("lt 获取失败")
        }
        val lt = input.attr("value").let {
            if (it == ""){
                throw IOException("lt 解析失败")
            }
        }
        val resp2 = APIModule.buildRequest(
            url = "http://218.6.163.95:18080/zfca/login;jsessionid=$jsId1",
            body = APIModule.buildFormBody(
                "useValidateCode" to 0,
                "isremenberme" to 0,
                "ip" to 0,
                "username" to username,
                "password" to password,
                "losetime" to 30,
                "lt" to lt,
                "_eventId" to "submit",
                "submit1" to "+"
            ),
            method = APIModule.METHOD_POST
        ).execute()
        val headers1 = resp2.headers
        val castgc = headers1["Set-Cookie"].run {
            if (this == null){
                Log.d(password)
                throw WrongPasswordException(username)
            }
            if (length <= 7){
                throw IOException("CASTGC 解析失败")
            }
            return@run substring(7, length - 1)
        }
        val location1 = headers1["Location"] ?: throw IOException("第一次跳转失败")
        val resp3 = APIModule.buildRequest(
            url = location1,
            method = APIModule.METHOD_GET
        ).execute()
        val headers2 = resp3.headers
        val jsId2 = headers2["Set-Cookie"].run {
            if (this == null){
                throw IOException("JSESSIONID2 获取失败")
            }
            if (length <= 11){
                throw IOException("JSESSIONID2 处理失败")
            }
            return@run substring(11, length - 1)
        }
        val location2 = headers2["Location"] ?: throw IOException("第二次跳转失败")
        val resp4 = APIModule.buildRequest(
            url = location2,
            cookies = APIModule.buildCookies(
                "JSESSIONID" to jsId1,
                "CASTGC" to castgc,
                "JSESSIONID" to jsId2
            ),
            method = APIModule.METHOD_GET
        ).execute()
        val headers3 = resp4.headers
        val location3 = headers3["Location"] ?: throw IOException("第三次跳转失败")
        val resp5 = APIModule.buildRequest(
            url = location3,
            cookies = APIModule.buildCookies(
                "JSESSIONID" to jsId2
            ),
            method = APIModule.METHOD_GET
        ).execute()
        val body = resp5.body?.string() ?: throw IOException("网络请求失败")
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
                throw IOException("identity 获取失败")
            }
        }
        location4.append("&login=0122579031373493708&url=xs_main.aspx")
        val resp6 = APIModule.buildRequest(
            url = location4.toString(),
            cookies = APIModule.buildCookies(
                "JSESSIONID" to jsId1,
                "CASTGC" to castgc,
                "JSESSIONID" to jsId2
            ),
            method = APIModule.METHOD_GET
        ).execute()
        val headers4 = resp6.headers
        val location5 = headers4["Location"] ?: throw IOException("第二次跳转失败")
        result.verifyLocation = location5
        Log.d(location5)
        resp1.close()
        resp2.close()
        resp3.close()
        resp4.close()
        resp5.close()
        resp6.close()
        return result
    }
}