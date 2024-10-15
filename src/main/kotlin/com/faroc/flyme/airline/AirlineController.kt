package com.faroc.flyme.airline

import com.faroc.flyme.airline.requests.AddAirlineRequest
import com.faroc.flyme.airline.responses.AirlinesResponse
import com.faroc.flyme.common.errors.problem
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
            description = "Added airlines.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
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

    @PostMapping("test")
    suspend fun addAirlines2() : MutableMap<String, String> {
        val a = mutableMapOf("a" to "b")

        return a
    }

    @GetMapping
    suspend fun fetchAirlines() : List<AirlinesResponse> {
        return service.fetchAirlines()
    }

    @GetMapping("{airlineId}")
    suspend fun fetchAirline(@PathVariable airlineId: Long) : ResponseEntity<*> {
        val result = service.fetchAirline(airlineId)

        return result.fold(
            success = { ResponseEntity.ok(result.value) },
            failure = { result.error.problem() }
        )
    }
}

