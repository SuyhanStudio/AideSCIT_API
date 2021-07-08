package com.sgpublic.aidescit.api.core.spring.property

import com.sgpublic.aidescit.api.core.util.Log
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "aidescit.token")
class TokenProperty {
    companion object {
        private lateinit var tokenKey: String
        private lateinit var tokenSecret: String
        private var accessExpired: Long = 2592000
        private var refreshExpired: Long = 124416000


        @JvmStatic
        val TOKEN_KEY: String get() = tokenKey
        @JvmStatic
        val TOKEN_SECRET: String get() = tokenSecret
        @JvmStatic
        val ACCESS_EXPIRED: Long get() = accessExpired
        @JvmStatic
        val REFRESH_EXPIRED: Long get() = refreshExpired
    }

    fun setTokenKey(value: String) {
        tokenKey = value
    }

    fun setTokenSecret(value: String) {
        tokenSecret = value
    }

    fun setAccessExpired(value: Long) {
        accessExpired = value
    }

    fun setRefreshExpired(value: Long) {
        refreshExpired = value
    }
}