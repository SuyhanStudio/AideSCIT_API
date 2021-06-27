package com.sgpublic.scit.tool

import com.sgpublic.scit.tool.api.util.ArgumentReader
import com.sgpublic.scit.tool.api.util.Log
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@SpringBootApplication
class Application {
    companion object {
        private var debug = false

        /** 是否为 Debug 环境 */
        val DEBUG: Boolean get() = debug

        @JvmStatic
        fun main(args: Array<String>) {
            val argsCurrent = setup(args)
            runApplication<Application>(*argsCurrent)
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

    class ServletInitializer : SpringBootServletInitializer() {
        override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
            return application.sources(Application::class.java)
        }
    }
}
