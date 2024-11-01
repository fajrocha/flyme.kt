package com.faroc.flyme.flights.infrastructure.repositories

import com.faroc.flyme.flights.domain.Flight
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FlightRepository : CoroutineCrudRepository<Flight, Long> {
}