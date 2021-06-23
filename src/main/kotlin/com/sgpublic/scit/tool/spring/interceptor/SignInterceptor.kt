package com.sgpublic.scit.tool.spring.interceptor

import com.sgpublic.scit.tool.api.exceptions.InvalidSignException
import com.sgpublic.scit.tool.api.manager.SignManager
import com.sgpublic.scit.tool.api.util.Log
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException
import org.springframework.web.servlet.HandlerInterceptor
import java.io.InputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SignInterceptor: HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        SignManager.calculate(request.parameterMap)

        return super.preHandle(request, response, handler)
    }
}