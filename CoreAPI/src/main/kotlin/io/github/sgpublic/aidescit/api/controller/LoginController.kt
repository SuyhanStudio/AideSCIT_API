package io.github.sgpublic.aidescit.api.controller

import io.github.sgpublic.aidescit.api.core.spring.BaseController
import io.github.sgpublic.aidescit.api.core.util.RSAUtil
import io.github.sgpublic.aidescit.api.core.util.TokenUtil
import io.github.sgpublic.aidescit.api.data.TokenPair
import io.github.sgpublic.aidescit.api.exceptions.InvalidPasswordFormatException
import io.github.sgpublic.aidescit.api.module.SessionModule
import io.github.sgpublic.aidescit.api.result.FailedResult
import io.github.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController: BaseController() {
    @Autowired
    lateinit var session: SessionModule

    @RequestMapping("/aidescit/login")
    fun login(username: String, password: String, sign: String): Map<String, Any?> {
        session.get(username, password)
        val passwordPre = RSAUtil.decode(password).apply {
            if (length <= 8) {
                throw InvalidPasswordFormatException()
            }
        }.substring(8)
        val token = TokenUtil.create(username, passwordPre)
        return SuccessResult(
            "access_token" to token.access,
            "refresh_token" to token.refresh
        )
    }

    @RequestMapping("/aidescit/login/springboard")
    fun springboard(
        @RequestParam(name = "access_token") token: String, sign: String
    ): Map<String, Any?> {
        val check = checkAccessToken(token)
        return SuccessResult("location" to session
            .getSpringboardLocation(check.getUsername()).verifyLocation)
    }

    @RequestMapping("/aidescit/login/token")
    fun refreshToken(@RequestParam(name = "access_token") access: String,
                     @RequestParam(name = "refresh_token") refresh: String,
                     sign: String): Map<String, Any?> {
        val check = TokenUtil.startVerify(TokenPair(access, refresh))
        if (!check.isAccessTokenActive()){
            return FailedResult.EXPIRED_TOKEN
        }
        if (check.isRefreshTokenExpired()){
            return FailedResult.EXPIRED_REFRESH_TOKEN
        }
        return if (!check.isAccessTokenExpired()){
            SuccessResult(
                "access_token" to access,
                "expired" to check.getAccessTokenValidTime()
            )
        } else {
            val newToken = check.refresh()
            SuccessResult(
                "access_token" to newToken,
                "expired" to TokenUtil.startVerify(TokenPair().apply {
                    this.access = newToken
                }).getAccessTokenValidTime()
            )
        }
    }
}