package com.sgpublic.scit.tool.api.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import org.springframework.context.annotation.Configuration

@Configuration
class Log {
    companion object {
        private val logger: Logger get() {
            val className = Throwable().stackTrace[2].className
            return LoggerFactory.getLogger(Class.forName(className))
        }

        private fun marker(username: String) = MarkerFactory.getMarker(username)

        /**
         * 普通 TRACE 日志
         */
        fun t(message: String){
            logger.trace(message)
        }

        /**
         * 附带用户名的 TRACE 日志
         */
        fun t(message: String, username: String){
            logger.trace(marker(username), message)
        }

        /**
         * 普通 DEBUG 日志
         */
        fun d(message: String){
            logger.debug(message)
        }

        /**
         * 附带用户名的 DEBUG 日志
         */
        fun d(message: String, username: String){
            logger.debug(marker(username), message)
        }

        /**
         * 普通 INFO 日志
         */
        fun i(message: String){
            logger.info(message)
        }

        /**
         * 附带用户名的 INFO 日志
         */
        fun i(message: String, username: String){
            logger.info(marker(username), message)
        }

        /**
         * 普通 WARN 日志
         */
        fun w(message: String){
            logger.warn(message)
        }

        /**
         * 附带用户名的 WARN 日志
         */
        fun w(message: String, username: String){
            logger.warn(marker(username), message)
        }

        /**
         * 附带 Throwable 的 WARN 日志
         */
        fun w(message: String, throwable: Throwable){
            logger.warn(message, throwable)
        }

        /**
         * 附带用户名和 Throwable 的 WARN 日志
         */
        fun w(message: String, username: String, throwable: Throwable){
            logger.warn(marker(username), message, throwable)
        }

        /**
         * 普通 ERROR 日志
         */
        fun e(message: String){
            logger.error(message)
        }

        /**
         * 附带用户名的 ERROR 日志
         */
        fun e(message: String, username: String){
            logger.warn(marker(username), message)
        }

        /**
         * 附带 Throwable 的 ERROR 日志
         */
        fun e(message: String, throwable: Throwable){
            logger.warn(message, throwable)
        }

        /**
         * 附带用户名和 Throwable 的 ERROR 日志
         */
        fun e(message: String, username: String, throwable: Throwable){
            logger.warn(marker(username), message, throwable)
        }
    }
}