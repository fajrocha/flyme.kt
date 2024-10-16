package com.faroc.flyme.airline.controllers.v1

import com.faroc.flyme.airline.services.AirlineService
import com.faroc.flyme.airline.requests.AddAirlineRequest
import com.faroc.flyme.airline.responses.AirlinesResponse
import com.faroc.flyme.common.errors.problem
import com.faroc.flyme.common.middleware.ValidationProblemDetail
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


@RestController("Airlines Controller V1")
@RequestMapping("v1/airlines")
@Tag(name = "Airlines")
class AirlineController(private val service: AirlineService) {
    @Operation(summary = "Add airlines to platform.", description = "Add airlines to platform")
    @ApiResponses(value = [
        ApiResponse(
                responseCode = "201",
                description = "Added airlines.",
                content = [(Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = AirlinesResponse::class))))
                ]),
        ApiResponse(
            responseCode = "400",
            description = "Validation error. Errors property will contain fields which are wrong.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ValidationProblemDetail::class)))
            ]),
        ApiResponse(
                responseCode = "500",
                description = "Failed to add airlines due to internal error.",
                content = [(Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ProblemDetail::class)))
                ]),
        ]
    )
    @PostMapping
    suspend fun addAirlines(
        @Valid @RequestBody request: List<AddAirlineRequest>) : ResponseEntity<List<AirlinesResponse>> {
        return ResponseEntity(service.addAirlines(request), HttpStatus.CREATED)
    }


    @Operation(summary = "Get airlines from the platform.", description = "Get available airlines from the platform")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Added airlines.",
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

    @Operation(summary = "Get airlines from the platform.", description = "Get available airlines from the platform")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Added airlines.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = AirlinesResponse::class)))
            ]),
        ApiResponse(
            responseCode = "404",
            description = "Added airlines.",
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
            failure = { result.error.problem() }
        )
    }
}

