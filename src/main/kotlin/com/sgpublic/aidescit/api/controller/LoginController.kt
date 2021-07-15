package com.sgpublic.aidescit.api.controller

import com.sgpublic.aidescit.api.core.util.TokenUtil
import com.sgpublic.aidescit.api.data.TokenPair
import com.sgpublic.aidescit.api.module.SessionModule
import com.sgpublic.aidescit.api.result.FailedResult
import com.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController {
    @Autowired
    lateinit var session: SessionModule

    @RequestMapping("/aidescit/login")
    fun login(username: String, password: String, sign: String): Map<String, Any> {
        session.get(username, password)
        val token = TokenUtil.create(username, password)
        return SuccessResult(
            "access_token" to token.accessToken,
            "refresh_token" to token.refreshToken
        )
    }

    @RequestMapping("/aidescit/login/springboard")
    fun springboard(token: TokenPair, sign: String): Map<String, Any> {
        val check = TokenUtil.startVerify(token)
        if (check.isAccessTokenExpired()){
            return FailedResult.EXPIRED_TOKEN
        }
        return SuccessResult(
            "location" to session.getVerifyLocation(check.getUsername()).verifyLocation
        )
    }

    @RequestMapping("/aidescit/login/token")
    fun refreshToken(token: TokenPair, sign: String): Map<String, Any> {
        val check = TokenUtil.startVerify(token)
        if (check.isAccessTokenActive() || check.isRefreshTokenActive()){
            return FailedResult.EXPIRED_TOKEN
        }
        if (check.isRefreshTokenExpired()){
            return FailedResult.EXPIRED_REFRESH_TOKEN
        }
        return if (!check.isAccessTokenExpired()){
            SuccessResult(
                "access_token" to token.accessToken,
                "expired" to check.getAccessTokenValidTime()
            )
        } else {
            val newToken = check.refresh()
            SuccessResult(
                "access_token" to newToken,
                "expired" to TokenUtil.startVerify(TokenPair().apply {
                    accessToken = newToken
                }).getAccessTokenValidTime()
            )
        }
    }
}