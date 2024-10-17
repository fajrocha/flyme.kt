package com.faroc.flyme.airlines.api.controllers.v1

import com.faroc.flyme.airlines.api.requests.AddAirlineRequest
import com.faroc.flyme.airlines.api.responses.AirlinesResponse
import com.faroc.flyme.airlines.services.AirlineService
import com.faroc.flyme.common.api.errors.toProblem
import com.faroc.flyme.common.api.middleware.ValidationProblemDetail
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
import org.springframework.web.bind.annotation.*


@RestController("Airlines Controller V1")
@RequestMapping("v1/airlines")
@Tag(name = "Airlines")
class AirlineController(private val service: AirlineService) {
    @Operation(summary = "Add airline.", description = "Add airline to platform")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Added airline.",
            content = [(Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = AirlinesResponse::class))))
            ]),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input. Errors property will contain fields which are wrong.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ValidationProblemDetail::class)))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to add airline due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ]
    )
    @PostMapping()
    suspend fun addAirlines2(
        @RequestBody @Valid request: AddAirlineRequest) : ResponseEntity<AirlinesResponse> {
        return ResponseEntity(service.addAirline(request), HttpStatus.CREATED)
    }

    @Operation(summary = "Fetch airlines.", description = "Fetch available airlines from the platform")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Fetched airlines successfully.",
            content = [(Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = AirlinesResponse::class))))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to fetch airlines due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ])
    @GetMapping
    suspend fun fetchAirlines() : List<AirlinesResponse> {
        return service.fetchAirlines()
    }

    @Operation(summary = "Fetch airline by id.", description = "Fetch airline by id.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Fetched airline successfully.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = AirlinesResponse::class)))
            ]),
        ApiResponse(
            responseCode = "404",
            description = "Airline requested not found.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to fetch airline due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ])
    @GetMapping("{airlineId}")
    suspend fun fetchAirline(@PathVariable airlineId: Long) : ResponseEntity<*> {
        val result = service.fetchAirline(airlineId)

        return result.fold(
            success = { ResponseEntity.ok(result.value) },
            failure = { r -> r.toProblem() }
        )
    }
}

