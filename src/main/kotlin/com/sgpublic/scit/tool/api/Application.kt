package com.sgpublic.scit.tool.api

import com.sgpublic.scit.tool.api.core.ArgumentReader
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

private var debug = false
private var logLevel: Int = 2

@SpringBootApplication
class Application {
    companion object {
        val DEBUG: Boolean get() = debug
        val LOG_LEVEL: Int get() = logLevel
    }
}

fun main(args: Array<String>) {
    val reader = ArgumentReader(args)
    debug = reader.containsItem("--debug")
    logLevel = arrayListOf("error", "warn", "info", "debug", "trace")
        .indexOf(reader.getString("log"))
    if (logLevel > 5 || logLevel < 0){
        logLevel = 2
    }

    runApplication<Application>(*args)
}

class ServletInitializer : SpringBootServletInitializer() {
    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(Application::class.java)
    }
}
