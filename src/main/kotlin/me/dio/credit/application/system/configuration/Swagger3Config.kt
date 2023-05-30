package me.dio.credit.application.system.configuration

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
public class Swagger3Config {
    @Bean
    fun publicApi(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
                .group("springcreditapplicationsystem-public")
                .pathsToMatch("/api/customer/**","/api/credits/**")
                .build()
    }
}
