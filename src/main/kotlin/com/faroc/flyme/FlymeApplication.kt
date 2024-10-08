package com.faroc.flyme

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FlymeApplication

fun main(args: Array<String>) {
	runApplication<FlymeApplication>(*args)
}
