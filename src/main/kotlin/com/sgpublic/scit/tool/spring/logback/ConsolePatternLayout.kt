package com.sgpublic.scit.tool.spring.logback

import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import com.sgpublic.scit.tool.spring.logback.converter.TraceConverter
import com.sgpublic.scit.tool.spring.logback.converter.UsernameConverter
import com.sgpublic.scit.tool.spring.logback.converter.UsernameConsoleConverter

/**
 * 添加自定义参数
 */
class ConsolePatternLayout: PatternLayout() {
    companion object {
        const val KEY_USERNAME = "username"
        const val KEY_FILE_NAME = "fileName"
        const val KEY_LINE_NUMBER = "lineNumber"

        init {
            defaultConverterMap["unc"] = UsernameConsoleConverter::class.java.name
            defaultConverterMap["unf"] = UsernameConverter::class.java.name
            defaultConverterMap["trace"] = TraceConverter::class.java.name
        }

        @JvmStatic
        fun parseArguments(args: Array<Any>?): Map<String, Any?> {
            val map = mutableMapOf<String, Any?>()
            if (args == null){
                return map
            }
            for (arg in args){
                if (arg !is Pair<*, *>){
                    continue
                }
                map.plus(arg)
            }
            return map
        }
    }
}