package com.sgpublic.aidescit.api.core.spring.logback

import ch.qos.logback.classic.PatternLayout
import com.sgpublic.aidescit.api.core.spring.logback.converter.TraceConverter
import com.sgpublic.aidescit.api.core.spring.logback.converter.UsernameConsoleConverter
import com.sgpublic.aidescit.api.core.spring.logback.converter.UsernameConverter

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
    }
}