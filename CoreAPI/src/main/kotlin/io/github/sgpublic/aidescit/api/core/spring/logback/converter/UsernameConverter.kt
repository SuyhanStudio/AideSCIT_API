package io.github.sgpublic.aidescit.api.core.spring.logback.converter

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent

/**
 * 输出到文件的参数 unf
 */
class UsernameConverter: ClassicConverter() {
    override fun convert(event: ILoggingEvent): String {
        event.marker?.let {
            return it.name
        }
        return ""
    }
}