package com.sgpublic.aidescit.api.core.spring.logback

import ch.qos.logback.classic.PatternLayout
import com.sgpublic.aidescit.api.core.spring.logback.converter.TraceConverter
import com.sgpublic.aidescit.api.core.spring.logback.converter.UsernameConverter
import com.sgpublic.aidescit.api.core.spring.logback.converter.UsernameConsoleConverter

/**
 * 添加自定义参数
 */
class ConsolePatternLayout: PatternLayout() {
    companion object {
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