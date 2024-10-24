package com.faroc.flyme.planes.api.controllers.v1

import com.faroc.flyme.common.api.errors.toProblem
import com.faroc.flyme.common.api.middleware.ValidationProblem
import com.faroc.flyme.common.api.requests.FetchPaginatedRequest
import com.faroc.flyme.planes.api.requests.PlaneRequest
import com.faroc.flyme.common.api.responses.PaginatedResponse
import com.faroc.flyme.planes.api.responses.PlaneResponse
import com.faroc.flyme.planes.services.PlaneService
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("Plane Controller V1")
@RequestMapping("v1/planes")
@Tag(name = "Planes")
class PlaneController(
    private val planeService: PlaneService
) {
    @Operation(summary = "Add plane.", description = "Add planes available on the platform.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Added plane model successfully.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = PlaneResponse::class)))
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
            responseCode = "500",
            description = "Failed to add plane due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)
            ))
            ]),
    ])
    @PostMapping
    suspend fun addPlane(
        @Valid @RequestBody planeRequest: PlaneRequest) : ResponseEntity<*> {
        val result = planeService.addPlane(planeRequest)

        return result.fold(
            success = { planeResponse -> ResponseEntity(planeResponse, HttpStatus.CREATED) },
            failure = { err -> err.toProblem() }
        )
    }

    @Operation(summary = "Fetch plane by id.", description = "Fetch plane by id.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Fetched plane successfully.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = PlaneResponse::class)))
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
            description = "Failed to fetch plane due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ])
    @GetMapping("{id}")
    suspend fun fetchPlane(@PathVariable id: Long) : ResponseEntity<*> {
        val result = planeService.fetchPlane(id)

        return result.fold(
            success = { planeResponse -> ResponseEntity.ok(planeResponse) },
            failure = { err -> err.toProblem() }
        )
    }

    @Operation(summary = "Fetch planes.", description = "Fetch planes.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Fetched planes successfully.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = PaginatedResponse::class)))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to fetch planes due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ])
    @GetMapping
    suspend fun fetchPlanes(
        @RequestParam("pageNumber", defaultValue = "1") pageNumber: Int,
        @RequestParam("pageSize", defaultValue = "5") pageSize: Int) : PaginatedResponse<PlaneResponse> {

        val request = FetchPaginatedRequest(pageNumber, pageSize)

        return planeService.fetchPlanes(request)
    }
}