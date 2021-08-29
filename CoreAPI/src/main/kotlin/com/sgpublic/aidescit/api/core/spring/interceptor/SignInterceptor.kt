package com.sgpublic.aidescit.api.core.spring.interceptor

import com.sgpublic.aidescit.api.Application
import com.sgpublic.aidescit.api.core.util.SignUtil
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 拦截器，用于 sign 校验，DEBUG 环境下或未提交 sign 参数时不校验
 */
class SignInterceptor: HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (!Application.DEBUG){
            SignUtil.calculate(request.parameterMap)
        }

        return super.preHandle(request, response, handler)
    }
}