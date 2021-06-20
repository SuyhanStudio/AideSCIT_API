package com.sgpublic.scit.tool.api.core.logback.filter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.AbstractMatcherFilter
import ch.qos.logback.core.spi.FilterReply
import com.sgpublic.scit.tool.api.Application

/**
 * 控制台输出过滤器
 */
open class ConsoleFilter: AbstractMatcherFilter<ILoggingEvent>() {
    companion object {
        @JvmStatic
        private val debug = Level.TRACE
        @JvmStatic
        private val product = Level.WARN
    }

    override fun decide(event: ILoggingEvent): FilterReply {
        if (!isStarted) {
            return FilterReply.NEUTRAL
        }
        return if (event.loggerName.startsWith("com.sgpublic")){
            filterOnSelf(event)
        } else {
            filterOnOther(event)
        }
    }

    /**
     * 若日志来自自己，则根据是否 DEBUG 过滤
     */
    private fun filterOnSelf(event: ILoggingEvent): FilterReply {
        val level = if (Application.DEBUG) debug else product
        return if (event.level.isGreaterOrEqual(level)) {
            FilterReply.NEUTRAL
        } else {
            FilterReply.DENY
        }
    }

    /**
     * 若日志来自外部，则始终按照等级 Level.WARN 过滤
     */
    private fun filterOnOther(event: ILoggingEvent): FilterReply {
        return if (event.level.isGreaterOrEqual(Level.WARN)) {
            FilterReply.NEUTRAL
        } else {
            FilterReply.DENY
        }
    }
}