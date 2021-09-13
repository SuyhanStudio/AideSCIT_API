package com.sgpublic.aidescit.api.core.spring

import com.sgpublic.aidescit.api.Application
import com.sgpublic.aidescit.api.core.util.Log
import com.sgpublic.aidescit.api.exceptions.*
import com.sgpublic.aidescit.api.result.FailedResult
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

/** 全局异常拦截器 */
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
    fun handleInvalidSignException(): Map<String, Any?> {
        return FailedResult.INVALID_SIGN
    }

    /**
     * 服务请求过期拦截
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ServiceExpiredException::class)
    fun handleServiceExpiredException(): Map<String, Any?>{
        return FailedResult.SERVICE_EXPIRED
    }

    /**
     * 用户密码错误拦截
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(WrongPasswordException::class)
    fun handleWrongPasswordException(): Map<String, Any?>{
        return FailedResult.WRONG_ACCOUNT
    }

    /**
     * 无效的 token 拦截
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpiredException(): Map<String, Any?>{
        return FailedResult.EXPIRED_TOKEN
    }


    /**
     * 容错处理，参数解析失败错误拦截
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(): Map<String, Any?> {
        return FailedResult.INTERNAL_SERVER_ERROR
    }

    /**
     * 容错处理，参数验证异常错误拦截
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(): Map<String, Any?> {
        return FailedResult.INTERNAL_SERVER_ERROR
    }

    /** 服务器非自身导致错误拦截 */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ServerRuntimeException::class)
    fun handleServerRuntimeException(e: Exception): Map<String, Any?> {
        Log.w("拦截错误，${e.message}", e)
        return FailedResult.SERVER_PROCESSING_ERROR
    }

    /** 容错处理，[TODO] 拦截 */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NotImplementedError::class)
    fun handleNotImplementedError(e: Exception): Map<String, Any?> {
        Log.d("拦截错误，${e.message}", e)
        return FailedResult.NOT_IMPLEMENTATION_ERROR
    }

    /** 容错处理，服务器内部处理错误拦截 */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IOException::class, JSONException::class)
    fun handleIOException(e: Exception): Map<String, Any?> {
        Log.d("拦截错误，${e.message}", e)
        return FailedResult.SERVER_PROCESSING_ERROR
    }

    /**
     * 拦截其余所有错误
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): Map<String, Any?> {
        Log.e("拦截错误，${e.message}", e)
        return FailedResult.INTERNAL_SERVER_ERROR
    }
}