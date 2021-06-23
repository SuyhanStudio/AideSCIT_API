package com.sgpublic.scit.tool.spring.logback.converter

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import com.sgpublic.scit.tool.spring.logback.ConsolePatternLayout

/**
 * 输出到控制台的参数 unc
 */
class UsernameConsoleConverter: ClassicConverter() {
    override fun convert(event: ILoggingEvent): String {
        event.marker?.let {
            return "[${it.name}] "
        }
        return ""
    }
}