package io.github.sgpublic.aidescit.api.core.spring.logback

import ch.qos.logback.classic.PatternLayout
import io.github.sgpublic.aidescit.api.core.spring.logback.converter.TraceConverter
import io.github.sgpublic.aidescit.api.core.spring.logback.converter.UsernameConsoleConverter
import io.github.sgpublic.aidescit.api.core.spring.logback.converter.UsernameConverter

/**
 * 添加自定义参数
 */
class ConsolePatternLayout: PatternLayout() {
    companion object {
        init {
            DEFAULT_CONVERTER_MAP["unc"] = UsernameConsoleConverter::class.java.name
            DEFAULT_CONVERTER_MAP["unf"] = UsernameConverter::class.java.name
            DEFAULT_CONVERTER_MAP["trace"] = TraceConverter::class.java.name
        }
    }
}