package com.faroc.flyme.airlines.infrastructure

import com.faroc.flyme.airlines.domain.Airline
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AirlineRepository : CoroutineCrudRepository<Airline, Long> {
    suspend fun findByName(name: String): Airline?
}