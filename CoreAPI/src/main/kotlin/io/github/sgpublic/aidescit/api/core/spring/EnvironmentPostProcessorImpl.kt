package io.github.sgpublic.aidescit.api.core.spring

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * 手动提前加载 properties
 */
class EnvironmentPostProcessorImpl: EnvironmentPostProcessor {
    companion object {
        /** 自定义配置文件列表 */
        private val PROFILES = arrayOf(
            "key.properties",
            "sql.properties",
            "token.properties",
            "semester.properties"
        )
    }

    /**
     * 加载配置，具体内容请查阅官网介绍
     * @see <a href="https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/htmlsingle/#boot-features-external-config">Externalized Configuration</a>
     */
    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication?) {
        val properties = Properties()
        for (profile in PROFILES) {
            val resource = File("config", profile).takeIf { it.exists() }
                ?: ClassPathResource(profile).file
            require(resource.exists()) { "Properties of $profile not found." }
            val property = try {
                properties.load(FileInputStream(resource))
                PropertiesPropertySource(profile, properties)
            } catch (e: IOException) {
                throw IllegalStateException( "Failed to load properties of $profile." , e)
            }
            environment.propertySources.addLast(property)
        }
    }
}