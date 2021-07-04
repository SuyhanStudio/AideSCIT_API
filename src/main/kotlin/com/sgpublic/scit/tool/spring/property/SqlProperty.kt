package com.sgpublic.scit.tool.spring.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "scit.edutool.datasource")
class SqlProperty {
    companion object {
        @JvmStatic
        var DRIVER_CLASS_NAME: String = ""
        @JvmStatic
        var URL: String = ""
        @JvmStatic
        var USERNAME: String = ""
        @JvmStatic
        var PASSNAME: String = ""
    }

    fun setDriverClassName(value: String) {
        DRIVER_CLASS_NAME = value
    }

    fun setUrl(value: String) {
        URL = value
    }

    fun setUsername(value: String) {
        USERNAME = value
    }

    fun setPassword(value: String) {
        PASSNAME = value
    }
}