package com.sgpublic.aidescit.api.core.spring

import com.sgpublic.aidescit.api.Application
import com.sgpublic.aidescit.api.exceptions.InvalidSignException
import com.sgpublic.aidescit.api.result.FailedResult
import com.sgpublic.aidescit.api.core.util.Log
import okio.IOException
import org.json.JSONException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/** 全局错误拦截器 */
@RestControllerAdvice
class GlobalExceptionHandler {
    /**
     * 拦截404，生产环境下跳转主页
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(exception: Exception, response: HttpServletResponse){
        if (Application.DEBUG){
            response.status = HttpStatus.NOT_FOUND.value()
            return
        }
        response.status = HttpStatus.MOVED_PERMANENTLY.value()
        response.setHeader("Location", "https://as.sgpublic.xyz/")
    }

    /**
     * 服务 Sign 错误拦截
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(InvalidSignException::class)
    fun handleInvalidSignException(): Map<String, Any> {
        return FailedResult.INVALID_SIGN
    }

    /**
     * 容错处理，参数解析失败错误拦截
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(exception: Exception, request: HttpServletRequest): Map<String, Any> {
        return FailedResult.INTERNAL_SERVER_ERROR
    }

    /**
     * 容错处理，参数验证异常错误拦截
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exception: Exception, request: HttpServletRequest): Map<String, Any> {
        return FailedResult.INTERNAL_SERVER_ERROR
    }

    /** 容错处理，服务器内部处理错误拦截 */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IOException::class, JSONException::class)
    fun handleIOException(): Map<String, Any> {
        return FailedResult.SERVER_PROCESSING_ERROR
    }

    /**
     * 拦截其余所有错误
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception, request: HttpServletRequest): Map<String, Any> {
        Log.e("拦截错误，${exception.message}", exception)
        return FailedResult.INTERNAL_SERVER_ERROR
    }
}