package com.sgpublic.aidescit.api.core.spring.property

import com.sgpublic.aidescit.api.core.util.Base64Util
import com.sgpublic.aidescit.api.core.util.Log
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 注入 sql.properties
 */
@Component
@ConfigurationProperties(prefix = "aidescit.datasource")
class SqlProperty {
    companion object {
        private lateinit var driverClassNameSetter: String
        private lateinit var urlSetter: String
        private lateinit var usernameSetter: String
        private lateinit var passwordSetter: String

        @JvmStatic
        val DRIVER_CLASS_NAME: String get() = driverClassNameSetter
        @JvmStatic
        val URL: String get() = urlSetter
        @JvmStatic
        val USERNAME: String get() = usernameSetter
        @JvmStatic
        val PASSWORD: String get() = passwordSetter
    }

    fun setDriverClassName(value: String) {
        driverClassNameSetter = value
    }

    fun setUrl(value: String) {
        urlSetter = value
    }

    fun setUsername(value: String) {
        usernameSetter = value
    }

    fun setPassword(value: String) {
        try {
            passwordSetter = Base64Util.decodeToString(value)
        } catch (e: Exception){
            Log.f("请将 aidescit.datasource.password 设置为经 Base64 加密后的字符串")
        }
    }
}