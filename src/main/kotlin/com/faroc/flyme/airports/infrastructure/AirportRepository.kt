package com.faroc.flyme.airports.infrastructure

import com.faroc.flyme.airports.domain.Airport
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AirportRepository : CoroutineCrudRepository<Airport, Long> {
    suspend fun existsByIataCode(iataCode: String): Boolean
}