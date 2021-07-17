package com.sgpublic.aidescit.api.core.spring

import com.sgpublic.aidescit.api.core.util.TokenUtil
import com.sgpublic.aidescit.api.data.TokenPair
import com.sgpublic.aidescit.api.exceptions.TokenExpiredException

abstract class BaseController {
    fun checkAccessToken(token: String): TokenUtil {
        return checkAccessToken(TokenPair().apply {
            accessToken = token
        })
    }

    fun checkAccessToken(token: TokenPair): TokenUtil {
        val check = TokenUtil.startVerify(token)
        if (check.isAccessTokenExpired()) {
            throw TokenExpiredException()
        }
        return check
    }
}