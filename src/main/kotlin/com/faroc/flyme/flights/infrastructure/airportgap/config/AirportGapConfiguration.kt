package com.faroc.flyme.flights.infrastructure.airportgap.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("airport-gap")
data class AirportGapConfiguration(
    val apiUrl: String,
    val apiToken: String = ""
)
