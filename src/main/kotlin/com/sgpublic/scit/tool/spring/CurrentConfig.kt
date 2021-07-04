package com.sgpublic.scit.tool.spring

import com.sgpublic.scit.tool.spring.interceptor.SignInterceptor
import com.sgpublic.scit.tool.spring.property.SqlProperty
import com.sgpublic.scit.tool.spring.property.TokenProperty
import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(TokenProperty::class, SqlProperty::class)
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
class CurrentConfig: WebMvcConfigurer {
    /** 添加自定义拦截器 */
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(SignInterceptor())
    }

    @Bean(name = ["dataSource"])
    fun getDataSource(): DataSource {
        val builder = DataSourceBuilder.create()
        builder.driverClassName(SqlProperty.DRIVER_CLASS_NAME)
        builder.url(SqlProperty.URL)
        builder.username(SqlProperty.USERNAME)
        builder.password(SqlProperty.PASSNAME)
        return builder.build()
    }
}