package io.github.sgpublic.aidescit.api.core.spring.logback.converter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.CompositeConverter

/**
 * 用于彩色 console 输出
 */
class ColoredConsoleConverter: CompositeConverter<ILoggingEvent>() {
    companion object {
        /** 灰色日志前缀 */
        private const val GRAY_FG = "\u001B[1;37;1m"
        /** 绿色日志前缀 */
        private const val GREEN_FG = "\u001B[1;36;1m"
        /** 蓝色日志前缀 */
        private const val BLUE_FG = "\u001B[1;32;1m"
        /** 黄色日志前缀 */
        private const val YELLOW_FG = "\u001B[1;33;1m"
        /** 红色日志前缀 */
        private const val RED_FG = "\u001B[1;31;1m"

        /** 日志后缀 */
        private const val END_FG = "\u001B[0m"
    }

    override fun transform(event: ILoggingEvent, input: String): String {
        return StringBuilder().append(when (event.level){
            Level.ERROR -> RED_FG
            Level.WARN -> YELLOW_FG
            Level.INFO -> BLUE_FG
            Level.DEBUG -> GREEN_FG
            Level.TRACE -> GRAY_FG
            else -> GRAY_FG
        }).append(input).append(END_FG).toString()
    }
}