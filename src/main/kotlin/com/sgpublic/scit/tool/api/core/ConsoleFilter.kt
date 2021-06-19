package com.sgpublic.scit.tool.api.core

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

class ConsoleFilter: Filter<ILoggingEvent>() {
    var level: Level? = null

    override fun decide(event: ILoggingEvent): FilterReply {
        if (!isStarted) {
            return FilterReply.NEUTRAL
        }

        return if (event.level.isGreaterOrEqual(level)) {
            FilterReply.NEUTRAL
        } else {
            FilterReply.DENY
        }
    }

    fun setLevel(level: String?) {
        this.level = Level.toLevel(level)
    }

    override fun start() {
        if (this.level != null) {
            super.start()
        }
    }
}