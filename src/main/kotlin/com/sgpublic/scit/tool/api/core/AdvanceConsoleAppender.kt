package com.sgpublic.scit.tool.api.core

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender

class AdvanceConsoleAppender: ConsoleAppender<ILoggingEvent>() {
    override fun subAppend(event: ILoggingEvent) {
//        val message = event.message
//        val context = event.loggerContextVO
        Throwable().printStackTrace()
        start()
        super.subAppend(event)
    }
}