package com.sgpublic.scit.tool.api.handler

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(response: HttpServletResponse){
        response.setHeader("Location", "https://scit.sgpublic.xyz/")
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception, request: HttpServletRequest): Map<String, Any> {
        println(exception::class.simpleName)
        return mutableMapOf(
            "code" to -200
        )
    }
}