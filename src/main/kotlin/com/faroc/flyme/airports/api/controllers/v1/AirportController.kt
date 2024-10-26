package com.faroc.flyme.airports.api.controllers.v1

import com.faroc.flyme.airports.api.request.AirportRequest
import com.faroc.flyme.airports.api.response.AirportResponse
import com.faroc.flyme.airports.services.AirportService
import com.faroc.flyme.common.api.errors.toProblem
import com.faroc.flyme.common.api.middleware.ValidationProblem
import com.github.michaelbull.result.fold
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("Airport Controller V1")
@RequestMapping("v1/airports")
@Tag(name = "Airport")
class AirportController(private val airportService: AirportService) {

    @Operation(summary = "Add airport.", description = "Add airport to the platform")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Added airport.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = AirportResponse::class)
            ))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input on request. Errors property will contain fields which are wrong.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ValidationProblem::class)
            ))
            ]),
        ApiResponse(
            responseCode = "409",
            description = "Conflict with existing record. The detail message will describe the issue.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to add airport due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)
            ))]),
    ])
    @PostMapping
    suspend fun addAirport(@Valid @RequestBody airportRequest: AirportRequest) : ResponseEntity<*> {
        val result = airportService.addAirport(airportRequest)

        return result.fold(
            success = { response -> ResponseEntity(response, HttpStatus.CREATED)},
            failure = { err -> err.toProblem() }
        )
    }

    @Operation(summary = "Fetch airport by id.", description = "Fetch airport by id.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Fetched airport successfully.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = AirportResponse::class)))
            ]),
        ApiResponse(
            responseCode = "404",
            description = "Airport requested not found.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to fetch airport due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ])
    @GetMapping("{id}")
    suspend fun fetchAirport(@PathVariable id: Long) : ResponseEntity<*> {
        val result = airportService.fetchAirport(id)

        return result.fold(
            success = { response -> ResponseEntity.ok(response)},
            failure = { err -> err.toProblem() }
        )
    }

    @Operation(summary = "Fetch airports.", description = "Fetch available airports from the platform")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Fetched airports successfully.",
            content = [(Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = AirportResponse::class))
            ))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to fetch airports due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ])
    @GetMapping
    suspend fun fetchAirports() : List<AirportResponse> {
        return airportService.fetchAirports()
    }
}