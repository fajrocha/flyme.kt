package com.faroc.flyme

import com.faroc.flyme.configurations.PostgresConfiguration
import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<FlymeApplication>().with(PostgresConfiguration::class).run(*args)
}
