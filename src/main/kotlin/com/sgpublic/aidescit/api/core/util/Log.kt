package com.sgpublic.aidescit.api.core.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

object Log {
    @JvmStatic
    private val logger: Logger get() {
        return LoggerFactory.getLogger(
            Class.forName(Throwable().stackTrace[2].className)
        )
    }

    /**
     * 生成日志标记
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    private fun marker(username: String): Marker {
        return MarkerFactory.getMarker(username)
    }

    /**
     * 普通 TRACE 日志
     * @param message 日志信息
     */
    @JvmStatic
    fun t(message: String){
        logger.trace(message)
    }

    /**
     * 附带用户名的 TRACE 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun t(message: String, username: String){
        logger.trace(marker(username), message)
    }

    /**
     * 普通 DEBUG 日志
     * @param message 日志信息
     */
    @JvmStatic
    fun d(message: String){
        logger.debug(message)
    }

    /**
     * 附带用户名的 DEBUG 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun d(message: String, username: String){
        logger.debug(marker(username), message)
    }

    /**
     * 普通 INFO 日志
     * @param message 日志信息
     */
    @JvmStatic
    fun i(message: String){
        logger.info(message)
    }

    /**
     * 附带用户名的 INFO 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun i(message: String, username: String){
        logger.info(marker(username), message)
    }

    /**
     * 普通 WARN 日志
     * @param message 日志信息
     */
    @JvmStatic
    fun w(message: String){
        logger.warn(message)
    }

    /**
     * 附带用户名的 WARN 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun w(message: String, username: String){
        logger.warn(marker(username), message)
    }

    /**
     * 附带 Throwable 的 WARN 日志
     * @param message 日志信息
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun w(message: String, throwable: Throwable){
        logger.warn(message, throwable)
    }

    /**
     * 附带用户名和 Throwable 的 WARN 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun w(message: String, username: String, throwable: Throwable){
        logger.warn(marker(username), message, throwable)
    }

    /**
     * 普通 ERROR 日志
     * @param message 日志信息
     */
    @JvmStatic
    fun e(message: String){
        logger.error(message)
    }

    /**
     * 附带用户名的 ERROR 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun e(message: String, username: String){
        logger.warn(marker(username), message)
    }

    /**
     * 附带 Throwable 的 ERROR 日志
     * @param message 日志信息
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun e(message: String, throwable: Throwable){
        logger.warn(message, throwable)
    }

    /**
     * 附带用户名和 Throwable 的 ERROR 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun e(message: String, username: String, throwable: Throwable){
        logger.warn(marker(username), message, throwable)
    }
}