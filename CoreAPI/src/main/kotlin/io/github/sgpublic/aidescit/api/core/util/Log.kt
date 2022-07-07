package io.github.sgpublic.aidescit.api.core.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import kotlin.system.exitProcess

/**
 * 自定义日志输出封装
 */
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
    fun t(message: Any){
        logger.trace(message.toString())
    }

    /**
     * 附带用户名的 TRACE 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun t(message: Any, username: String){
        logger.trace(marker(username), message.toString())
    }

    /**
     * 普通 DEBUG 日志
     * @param message 日志信息
     */
    @JvmStatic
    fun d(message: Any){
        logger.debug(message.toString())
    }

    /**
     * 附带用户名的 DEBUG 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun d(message: Any, username: String){
        logger.debug(marker(username), message.toString())
    }

    /**
     * 附带 Throwable 的 DEBUG 日志
     * @param message 日志信息
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun d(message: Any, throwable: Throwable){
        logger.debug(message.toString(), throwable)
    }

    /**
     * 附带用户名和 Throwable 的 DEBUG 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun d(message: Any, username: String, throwable: Throwable){
        logger.debug(marker(username), message.toString(), throwable)
    }

    /**
     * 普通 INFO 日志
     * @param message 日志信息
     */
    @JvmStatic
    fun i(message: Any){
        logger.info(message.toString())
    }

    /**
     * 附带用户名的 INFO 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun i(message: Any, username: String){
        logger.info(marker(username), message.toString())
    }

    /**
     * 普通 WARN 日志
     * @param message 日志信息
     */
    @JvmStatic
    fun w(message: Any){
        logger.warn(message.toString())
    }

    /**
     * 附带用户名的 WARN 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun w(message: Any, username: String){
        logger.warn(marker(username), message.toString())
    }

    /**
     * 附带 Throwable 的 WARN 日志
     * @param message 日志信息
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun w(message: Any, throwable: Throwable){
        logger.warn(message.toString(), throwable)
    }

    /**
     * 附带用户名和 Throwable 的 WARN 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun w(message: Any, username: String, throwable: Throwable){
        logger.warn(marker(username), message.toString(), throwable)
    }

    /**
     * 普通 ERROR 日志
     * @param message 日志信息
     */
    @JvmStatic
    fun e(message: Any){
        logger.error(message.toString())
    }

    /**
     * 附带用户名的 ERROR 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun e(message: Any, username: String){
        logger.warn(marker(username), message.toString())
    }

    /**
     * 附带 Throwable 的 ERROR 日志
     * @param message 日志信息
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun e(message: Any, throwable: Throwable){
        logger.warn(message.toString(), throwable)
    }

    /**
     * 附带用户名和 Throwable 的 ERROR 日志
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun e(message: Any, username: String, throwable: Throwable){
        logger.warn(marker(username), message.toString(), throwable)
    }

    /**
     * 普通 ERROR 日志，打印后结束进程
     * @param message 日志信息
     */
    @JvmStatic
    fun f(message: Any){
        logger.error(message.toString())
        exitProcess(0)
    }

    /**
     * 附带用户名的 ERROR 日志，打印后结束进程
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     */
    @JvmStatic
    fun f(message: Any, username: String){
        logger.warn(marker(username), message.toString())
        exitProcess(0)
    }

    /**
     * 附带 Throwable 的 ERROR 日志，打印后结束进程
     * @param message 日志信息
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun f(message: Any, throwable: Throwable){
        logger.warn(message.toString(), throwable)
        exitProcess(0)
    }

    /**
     * 附带用户名和 Throwable 的 ERROR 日志，打印后结束进程
     * @param message 日志信息
     * @param username 产生日志用户的用户名
     * @param throwable 日志附带的异常堆栈信息
     */
    @JvmStatic
    fun f(message: Any, username: String, throwable: Throwable){
        logger.warn(marker(username), message.toString(), throwable)
        exitProcess(0)
    }
}