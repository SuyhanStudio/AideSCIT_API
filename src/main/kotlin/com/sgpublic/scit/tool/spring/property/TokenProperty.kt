package com.sgpublic.scit.tool.spring.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "scit.edutool.token")
class TokenProperty {
    companion object {
        @JvmStatic
        var TOKEN_KEY: String = ""
        @JvmStatic
        var TOKEN_SECRET: String = ""
        @JvmStatic
        var ACCESS_EXPIRED: Long = 2592000
        @JvmStatic
        var REFRESH_EXPIRED: Long = 124416000
    }

    fun setTokenKey(value: String) {
        TOKEN_KEY = value
    }

    fun setTokenSecret(value: String) {
        TOKEN_SECRET = value
    }

    fun setAccessExpired(value: Long) {
        ACCESS_EXPIRED = value
    }

    fun setRefreshExpired(value: Long) {
        REFRESH_EXPIRED = value
    }
}