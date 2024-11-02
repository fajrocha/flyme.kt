package com.faroc.flyme.flights.api.controllers

import com.faroc.flyme.common.api.errors.toProblem
import com.faroc.flyme.common.api.requests.FetchPaginatedRequest
import com.faroc.flyme.common.api.responses.PaginatedResponse
import com.faroc.flyme.flights.api.requests.ScheduleFlightRequest
import com.faroc.flyme.flights.api.responses.FlightDetailsResponse
import com.faroc.flyme.flights.services.FlightsFetcherService
import com.faroc.flyme.flights.services.FlightsSchedulerService
import com.github.michaelbull.result.fold
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/flights")
class FlightsController(
    private val schedulerService: FlightsSchedulerService,
    private val fetcherService: FlightsFetcherService,
) {
    @PostMapping
    suspend fun fetchAirportDistance(
        @Valid @RequestBody scheduleFlightRequest: ScheduleFlightRequest
    ) : ResponseEntity<*> {
        val result = schedulerService.scheduleFlight(scheduleFlightRequest)

        return result.fold(
            { flightScheduled -> ResponseEntity(flightScheduled, HttpStatus.CREATED) },
            { err -> err.toProblem() }
        )
    }

    @GetMapping("{flightId}")
    suspend fun fetchAirportDistance(@PathVariable flightId: Long) : ResponseEntity<*> {
        val result = fetcherService.fetchFlightById(flightId)

        return result.fold(
            { flightScheduled -> ResponseEntity(flightScheduled, HttpStatus.CREATED) },
            { err -> err.toProblem() }
        )
    }

    @GetMapping
    suspend fun fetchPlanes(
        @RequestParam("pageNumber", defaultValue = "1") pageNumber: Int,
        @RequestParam("pageSize", defaultValue = "10") pageSize: Int) : PaginatedResponse<FlightDetailsResponse> {

        val request = FetchPaginatedRequest(pageNumber, pageSize)

        return fetcherService.fetchFlights(request)
    }
}