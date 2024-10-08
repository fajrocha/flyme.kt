package com.faroc.flyme.airline

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AirlineRepository : CoroutineCrudRepository<Airline, Long> {
}