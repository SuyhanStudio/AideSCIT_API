package com.sgpublic.scit.tool

import com.sgpublic.scit.tool.api.util.ArgumentReader
import com.sgpublic.scit.tool.api.util.Log
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class Application {
    companion object {
        private var debug = false

        /** 是否为 Debug 环境 */
        val DEBUG: Boolean get() = debug

        @JvmStatic
        fun main(args: Array<String>) {
            setup(args)
            runApplication<Application>(*args)
        }

        /** 初始化参数 */
        private fun setup(args: Array<String>){
            val reader = ArgumentReader(args)
            debug = reader.containsItem("--debug")
        }
    }

    class ServletInitializer : SpringBootServletInitializer() {
        override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
            return application.sources(Application::class.java)
        }
    }
}
