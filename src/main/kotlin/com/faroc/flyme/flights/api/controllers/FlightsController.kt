package com.faroc.flyme.flights.api.controllers

import com.faroc.flyme.common.api.errors.toProblem
import com.faroc.flyme.common.api.middleware.ValidationProblem
import com.faroc.flyme.common.api.requests.FetchPaginatedRequest
import com.faroc.flyme.common.api.responses.PaginatedResponse
import com.faroc.flyme.flights.api.requests.ScheduleFlightRequest
import com.faroc.flyme.flights.api.responses.FlightDetailsResponse
import com.faroc.flyme.flights.api.responses.ScheduleFlightResponse
import com.faroc.flyme.flights.services.FlightsFetcherService
import com.faroc.flyme.flights.services.FlightsSchedulerService
import com.github.michaelbull.result.fold
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController("Flights Controller V1")
@RequestMapping("v1/flights")
@Tag(name = "Flights")
class FlightsController(
    private val schedulerService: FlightsSchedulerService,
    private val fetcherService: FlightsFetcherService,
) {
    @Operation(summary = "Add flight.", description = "Add flights available on the platform.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Added flight successfully.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ScheduleFlightResponse::class)
            ))
            ]),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input. Errors property will contain fields which are wrong.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ValidationProblem::class)
            ))
            ]),
        ApiResponse(
            responseCode = "404",
            description = "Entities in the request like airports, airlines or plane not found.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)
            ))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to add flight due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)
            ))
            ]),
    ])
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

    @Operation(summary = "Fetch flight by id.", description = "Fetch flight by id.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Fetched flight successfully.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = FlightDetailsResponse::class)))
            ]),
        ApiResponse(
            responseCode = "404",
            description = "Plane requested not found.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to fetch flight due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ])
    @GetMapping("{flightId}")
    suspend fun fetchAirportDistance(@PathVariable flightId: Long) : ResponseEntity<*> {
        val result = fetcherService.fetchFlightById(flightId)

        return result.fold(
            { flightScheduled -> ResponseEntity(flightScheduled, HttpStatus.CREATED) },
            { err -> err.toProblem() }
        )
    }

    @Operation(summary = "Fetch flights.", description = "Fetch flights available on platform.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Fetched flights successfully.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = PaginatedResponse::class)))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to fetch flights due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ])
    @GetMapping
    suspend fun fetchPlanes(
        @RequestParam("pageNumber", defaultValue = "1") pageNumber: Int,
        @RequestParam("pageSize", defaultValue = "10") pageSize: Int) : PaginatedResponse<FlightDetailsResponse> {

        val request = FetchPaginatedRequest(pageNumber, pageSize)

        return fetcherService.fetchFlights(request)
    }
}