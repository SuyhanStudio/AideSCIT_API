package com.sgpublic.scit.tool.spring

import com.sgpublic.scit.tool.spring.interceptor.SignInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class Configuration: WebMvcConfigurer {

    /** 添加自定义拦截器 */
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(SignInterceptor())
    }
}