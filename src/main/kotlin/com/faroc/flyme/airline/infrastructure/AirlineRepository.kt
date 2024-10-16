package com.faroc.flyme.airline.infrastructure

import com.faroc.flyme.airline.domain.Airline
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AirlineRepository : CoroutineCrudRepository<Airline, Long> {
}