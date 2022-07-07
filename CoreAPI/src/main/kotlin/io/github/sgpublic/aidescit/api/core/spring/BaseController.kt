package io.github.sgpublic.aidescit.api.core.spring

import io.github.sgpublic.aidescit.api.core.util.TokenUtil
import io.github.sgpublic.aidescit.api.data.TokenPair
import io.github.sgpublic.aidescit.api.exceptions.TokenExpiredException

abstract class BaseController {
    fun checkAccessToken(token: String): TokenUtil {
        return checkAccessToken(TokenPair().apply {
            access = token
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