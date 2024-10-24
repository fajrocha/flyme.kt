package com.faroc.flyme.common.api.openapi

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {
    @Bean
    fun customOpenApi(): OpenAPI {
        val title = "Flyme REST API"

        return OpenAPI().info(
            Info().title(title)
        )
    }
}