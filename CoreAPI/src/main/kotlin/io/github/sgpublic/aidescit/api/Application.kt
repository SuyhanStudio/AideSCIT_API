package io.github.sgpublic.aidescit.api

import io.github.sgpublic.aidescit.api.core.spring.CurrentConfig
import io.github.sgpublic.aidescit.api.core.util.ArgumentReader
import io.github.sgpublic.aidescit.api.core.util.Log
import io.github.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import

/**
 * SpringBootApplication
 */
@SpringBootApplication
@Import(CurrentConfig::class)
class Application {
    companion object {
        /** 启动入口 */
        @JvmStatic
        fun main(args: Array<String>) {
            context = runApplication<Application>(*setup(args))
            Log.i("服务启动成功！")
        }

        /** 是否为 Debug 环境 */
        private var debug = false
        val DEBUG: Boolean get() = debug

        /** ApplicationContext */
        private lateinit var context: ApplicationContext
        val CONTEXT: ApplicationContext get() = context

        /** 获取 Bean */
        inline fun <reified T> getBean(bean: String): T {
            val beanObject = CONTEXT.getBean(bean)
            if (beanObject !is T) {
                throw ServerRuntimeException.INTERNAL_ERROR
            }
            return beanObject
        }

        /** 初始化 SpringApplication 参数 */
        class ServletInitializer : SpringBootServletInitializer() {
            override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
                return application.apply {
                    lazyInitialization(true)
                    sources(Application::class.java)
                }
            }
        }

        /** 初始化参数 */
        private fun setup(args: Array<String>): Array<String> {
            val argsCurrent = arrayListOf<String>()
            argsCurrent.addAll(args)
            val reader = ArgumentReader(args)
            debug = reader.containsItem("--debug")
            if (reader.getString("--spring.profiles.active", null) == null) {
                val arg = StringBuilder("--spring.profiles.active=")
                if (debug){
                    arg.append("dev")
                } else {
                    arg.append("pro")
                }
                argsCurrent.add(arg.toString())
            }
            return Array(argsCurrent.size) {
                return@Array argsCurrent[it]
            }
        }
    }
}
