package com.sgpublic.aidescit.api.core.util

import com.sgpublic.aidescit.api.Application
import com.sgpublic.aidescit.api.core.spring.property.TokenProperty
import com.sgpublic.aidescit.api.data.TokenPair
import com.sgpublic.aidescit.api.exceptions.InvalidPasswordFormatException
import com.sgpublic.aidescit.api.exceptions.InvalidRefreshTokenException
import com.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import com.sgpublic.aidescit.api.mariadb.dao.UserSessionRepository
import com.sgpublic.aidescit.api.module.APIModule
import org.json.JSONObject

/**
 * token 管理，支持生成和检查与刷新 token
 */
class TokenUtil private constructor(private val token: TokenPair){
    private var username: String = ""
    private var password: String = ""
    private var tokenCreateTime: Long = 0
    private var accessTokenActive: Boolean = false
    private var refreshTokenActive: Boolean = false

    /**
     * access_token 是否有效，忽略其时效性
     */
    fun isAccessTokenActive(): Boolean {
        return accessTokenActive
    }

    /**
     * access_token 是否过期
     */
    fun isAccessTokenExpired(): Boolean {
        if (!accessTokenActive){
            return true
        }
        return getAccessTokenValidTime() < 0
    }

    fun getAccessTokenValidTime(): Long {
        return this.tokenCreateTime + TokenProperty.ACCESS_EXPIRED - APIModule.TS
    }

    /**
     * refresh_token 是否有效，忽略其时效性
     */
    fun isRefreshTokenActive(): Boolean {
        return refreshTokenActive
    }

    /**
     * refresh_token 是否过期
     */
    fun isRefreshTokenExpired(): Boolean {
        if (!refreshTokenActive){
            return true
        }
        return this.tokenCreateTime + TokenProperty.REFRESH_EXPIRED < APIModule.TS
    }

    /**
     * 获取 token 中附带的用户名
     */
    fun getUsername(): String {
        return username
    }

    /**
     * 刷新 token
     */
    fun refresh(): String {
        if (isRefreshTokenExpired()){
            throw InvalidRefreshTokenException()
        }
        if (!isAccessTokenExpired()){
            return token.access
        }
        return create(username, password).access
    }

    companion object {
        private val userSession: UserSessionRepository get() {
            return Application.getBean("userSessionRepository")
        }

        /**
         * 开始 token 检查
         * @param token 欲检查的 token 对，若想检查 refresh_token 是否有效必须保证 access_token 不为空
         * @return 返回 [TokenUtil]
         */
        @JvmStatic
        fun startVerify(token: TokenPair): TokenUtil {
            val result = TokenUtil(token)
            if (token.access == ""){
                Log.d("access_token 为空")
                return result
            }
            val accessPre = token.access.split(".")
            if (accessPre.size != 3){
                Log.d("access_token 格式错误")
                return result
            }
            val header = Base64Util.decodeToString(accessPre[1])
                .replace("%", "")
                .split("&")
            if (header.size != 2){
                Log.d("access_token header 格式错误")
                return result
            }
            result.username = header[0]
            val password = userSession.getUserPassword(result.username)
                ?: throw ServerRuntimeException.INTERNAL_ERROR
            result.password = RSAUtil.decode(password).apply {
                if (length <= 8){
                    Log.d("用户密码解析错误")
                    return result
                }
            }.substring(8)
            result.tokenCreateTime = header[1].toLongOrNull().also {
                if (it == null){
                    Log.d("token 创建时间解析错误")
                    return result
                }
            }!!
            val bodyPre = buildTokenBody(result.password, result.tokenCreateTime)
            val accessBody = MD5Util.encode(bodyPre.apply {
                put("type", "access")
            }.toString())
            if (accessBody != accessPre[0]){
                Log.d("access_token body 无效")
                return result
            }
            val accessCheckPre = "$accessBody.${accessPre[1]}.${TokenProperty.TOKEN_SECRET}"
            if (MD5Util.encode(accessCheckPre) != accessPre[2]){
                Log.d("access_token 签名无效")
                return result
            }
            result.accessTokenActive = true

            if (token.refresh == ""){
                return result
            }

            val refreshPre = token.refresh.split(".")
            if (refreshPre.size != 2){
                Log.d("refresh_token 格式错误")
                return result
            }
            val refreshBody = MD5Util.encode(bodyPre.apply {
                put("type", "refresh")
            }.toString())
            if (refreshBody != refreshPre[0]){
                Log.d("refresh_token body 无效")
                return result
            }
            val refreshCheckPre = "$refreshBody.${accessPre[1]}.${TokenProperty.TOKEN_SECRET}"
            if (MD5Util.encodeFull(refreshCheckPre) != refreshPre[1]){
                Log.d("refresh_token 签名无效")
                return result
            }
            result.refreshTokenActive = true
            return result
        }

        /**
         * 生成 token 对
         * @param username 用户名
         * @param password 用户加盐密文密码
         * @return 返回 [TokenPair]
         */
        @JvmStatic
        fun create(username: String, password: String): TokenPair {
            val time = APIModule.TS

            val token = TokenPair()
            val headerPre = StringBuilder()
                .append(username)
                .append("&")
                .append(time)
            while (headerPre.length % 3 != 0){
                headerPre.append("%")
            }
            val header = Base64Util.encodeToString(headerPre)

            val bodyPre = buildTokenBody(password, time)
            val accessBodyPre = bodyPre.apply {
                put("type", "access")
            }.toString()
            val refreshBodyPre = bodyPre.apply {
                put("type", "refresh")
            }.toString()
            token.access = "${MD5Util.encode(accessBodyPre)}.$header."
            token.refresh = "${MD5Util.encode(refreshBodyPre)}."

            val accessFooterPre = "${token.access}${TokenProperty.TOKEN_SECRET}"
            val refreshFooterPre = "${token.refresh}$header.${TokenProperty.TOKEN_SECRET}"

            token.access += MD5Util.encode(accessFooterPre)
            token.refresh += MD5Util.encodeFull(refreshFooterPre)

            return token
        }

        /**
         * 内部方法，生成 token 的 body 预备内容对象
         */
        @JvmStatic
        private fun buildTokenBody(password: String, time: Long): JSONObject {
            return JSONObject().apply {
                put("password", password)
                put("time", time)
                put("key", TokenProperty.TOKEN_KEY)
            }
        }
    }
}