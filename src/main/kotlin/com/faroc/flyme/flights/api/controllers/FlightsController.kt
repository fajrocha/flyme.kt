package com.faroc.flyme.flights.api.controllers

import com.faroc.flyme.common.api.errors.toProblem
import com.faroc.flyme.flights.api.requests.ScheduleFlightRequest
import com.faroc.flyme.flights.services.FlightService
import com.github.michaelbull.result.fold
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@RestController
@RequestMapping("v1/flights")
class FlightsController(private val service: FlightService) {
    @PostMapping
    suspend fun fetchAirportDistance(
        @Valid @RequestBody scheduleFlightRequest: ScheduleFlightRequest
    ) : ResponseEntity<*> {
        val result = service.scheduleFlight(scheduleFlightRequest)

        return result.fold(
            { flightScheduled -> ResponseEntity(flightScheduled, HttpStatus.CREATED) },
            { err -> err.toProblem() }
        )
    }

    @GetMapping
    suspend fun fetchAirportDistance() : Test {
        val a = Instant.now()

        val b = ZonedDateTime.ofInstant(a, ZoneId.of("America/Los_Angeles"))

        return Test(b)
    }
}

data class Test(val time: ZonedDateTime)