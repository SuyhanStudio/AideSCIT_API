package com.sgpublic.aidescit.api.core.spring.interceptor

import com.sgpublic.aidescit.api.manager.SignManager
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SignInterceptor: HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        SignManager.calculate(request.parameterMap)

        return super.preHandle(request, response, handler)
    }
}