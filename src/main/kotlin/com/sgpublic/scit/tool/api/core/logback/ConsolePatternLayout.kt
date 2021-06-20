package com.sgpublic.scit.tool.api.core.logback

import ch.qos.logback.classic.PatternLayout
import com.sgpublic.scit.tool.api.core.logback.converter.UsernameConverter
import com.sgpublic.scit.tool.api.core.logback.converter.UsernameConsoleConverter

/**
 * 添加自定义参数
 */
class ConsolePatternLayout: PatternLayout() {
    companion object {
        init {
            defaultConverterMap["unc"] = UsernameConsoleConverter::class.java.name
            defaultConverterMap["unf"] = UsernameConverter::class.java.name
        }
    }
}