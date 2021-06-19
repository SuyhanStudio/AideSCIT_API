package com.sgpublic.scit.tool.api.core

import com.sgpublic.scit.tool.api.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration

@Configuration
class Log {
    companion object {
        private val loggers: MutableMap<String, Logger> = mutableMapOf()

        private val logger: Logger get() {
            val className = Throwable().stackTrace[2].className
            loggers[className]?.let {
                return it
            }
            val logger = LoggerFactory.getLogger(Class.forName(className))
            loggers[className] = logger
            return logger
        }

        fun t(message: String){
            if (Application.LOG_LEVEL < 5){
                return
            }
            logger.trace(message)
        }

        fun d(message: String){
            if (Application.LOG_LEVEL < 4){
                return
            }
            logger.debug(message)
        }

        fun i(message: String){
            if (Application.LOG_LEVEL < 3){
                return
            }
            logger.info(message)
        }

        fun w(message: String){
            if (Application.LOG_LEVEL < 2){
                return
            }
            logger.warn(message)
        }

        fun e(message: String){
            if (Application.LOG_LEVEL < 1){
                return
            }
            logger.error(message)
        }
    }
}