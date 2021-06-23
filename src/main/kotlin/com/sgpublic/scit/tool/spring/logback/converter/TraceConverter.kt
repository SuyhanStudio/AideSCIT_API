package com.sgpublic.scit.tool.spring.logback.converter

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import com.sgpublic.scit.tool.api.util.Log
import com.sgpublic.scit.tool.spring.logback.ConsolePatternLayout

/** 日志定位 */
class TraceConverter: ClassicConverter() {
    override fun convert(event: ILoggingEvent): String {
        val judge = event.callerData[0].className.startsWith(Log::class.java.name)
        val callerData = if (judge){
            event.callerData[1]
        } else {
            event.callerData[0]
        }
        return "${callerData.fileName}:${callerData.lineNumber}"
    }
}