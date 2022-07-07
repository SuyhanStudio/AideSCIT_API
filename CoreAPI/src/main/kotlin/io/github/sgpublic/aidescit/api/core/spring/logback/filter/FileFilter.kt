package io.github.sgpublic.aidescit.api.core.spring.logback.filter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.AbstractMatcherFilter
import ch.qos.logback.core.spi.FilterReply

/**
 * 日志文件过滤器
 */
class FileFilter : AbstractMatcherFilter<ILoggingEvent>() {
    /**
     * 始终按照等级 Level.WARN 过滤
     */
    override fun decide(event: ILoggingEvent): FilterReply {
        if (!isStarted) {
            return FilterReply.NEUTRAL
        }
        return if (event.level.isGreaterOrEqual(Level.WARN)) {
            FilterReply.NEUTRAL
        } else {
            FilterReply.DENY
        }
    }
}