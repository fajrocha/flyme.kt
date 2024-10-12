package com.faroc.flyme.airline

import com.faroc.flyme.airline.domain.Airline
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AirlineRepository : CoroutineCrudRepository<Airline, Long> {
}