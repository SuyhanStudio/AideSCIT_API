package io.github.sgpublic.aidescit.api.core.spring.property

import io.github.sgpublic.aidescit.api.core.util.Base64Util
import io.github.sgpublic.aidescit.api.core.util.Log
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 注入 sql.properties
 */
@Component
@ConfigurationProperties(prefix = "aidescit.datasource")
class SqlProperty {
    companion object {
        private lateinit var driverClassName: String
        private lateinit var platform: String
        private lateinit var url: String
        private lateinit var username: String
        private lateinit var password: String

        @JvmStatic
        val DRIVER_CLASS_NAME: String get() = driverClassName
        @JvmStatic
        val DATABASE_PLATFORM: String get() = platform
        @JvmStatic
        val URL: String get() = url
        @JvmStatic
        val USERNAME: String get() = username
        @JvmStatic
        val PASSWORD: String get() = password
    }

    fun setDriverClassName(value: String) {
        driverClassName = value
    }

    fun setDatabasePlatform(value: String) {
        platform = value
    }

    fun setUrl(value: String) {
        url = value
    }

    fun setUsername(value: String) {
        username = value
    }

    fun setPassword(value: String) {
        try {
            password = Base64Util.decodeToString(value)
        } catch (e: Exception){
            Log.f("请将 aidescit.datasource.password 设置为经 Base64 加密后的字符串")
        }
    }
}