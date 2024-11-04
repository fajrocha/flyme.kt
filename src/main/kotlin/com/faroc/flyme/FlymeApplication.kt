package com.faroc.flyme

import com.faroc.flyme.common.infrastructure.airportgap.config.AirportGapConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AirportGapConfiguration::class)
class FlymeApplication

fun main(args: Array<String>) {
	runApplication<FlymeApplication>(*args)
}
