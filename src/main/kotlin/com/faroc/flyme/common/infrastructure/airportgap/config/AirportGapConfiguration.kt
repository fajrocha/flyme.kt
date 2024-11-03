package com.faroc.flyme.common.infrastructure.airportgap.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("airport-gap")
data class AirportGapConfiguration(
    val apiUrl: String,
    val port: Int,
    val apiToken: String = ""
)
