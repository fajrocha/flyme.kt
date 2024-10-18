package com.faroc.flyme.planes.api.controllers.v1

import com.faroc.flyme.airlines.api.responses.AirlinesResponse
import com.faroc.flyme.common.api.errors.toProblem
import com.faroc.flyme.common.api.middleware.ValidationProblem
import com.faroc.flyme.planes.api.requests.PlaneModelRequest
import com.faroc.flyme.planes.api.responses.PlaneModelResponse
import com.faroc.flyme.planes.services.PlaneModelService
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

@RestController("PlaneModel Controller V1")
@RequestMapping("v1/plane-model")
@Tag(name = "Plane Models")
class PlaneModelController(private val planeModelService: PlaneModelService) {

    @Operation(summary = "Add plane models.", description = "Add plane models available on the platform.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Added airlines.",
            content = [(Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = PlaneModelResponse::class))
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
            responseCode = "500",
            description = "Failed to add plane models due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)
            ))
            ]),
    ])
    @PostMapping
    suspend fun addPlaneModels(
        @RequestBody
        @Valid
        addPlaneModelsRequest: PlaneModelRequest) : ResponseEntity<PlaneModelResponse> {

        return ResponseEntity(planeModelService.addPlaneModel(addPlaneModelsRequest), HttpStatus.CREATED)
    }

    @Operation(summary = "Fetch plane models.", description = "Fetch plane models available on the platform.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Fetched plane models successfully.",
            content = [(Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = PlaneModelResponse::class))))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to fetch plane models due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ])
    @GetMapping
    suspend fun fetchPlaneModels() : List<PlaneModelResponse> {
        return planeModelService.fetchPlaneModels()
    }

    @Operation(summary = "Fetch plane model by id.", description = "Fetch plane models by id.")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Fetched plane model successfully.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = AirlinesResponse::class)))
            ]),
        ApiResponse(
            responseCode = "404",
            description = "Plane model requested not found.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
        ApiResponse(
            responseCode = "500",
            description = "Failed to fetch plane model due to internal error.",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProblemDetail::class)))
            ]),
    ])
    @GetMapping("{planeModelId}")
    suspend fun fetchPlaneModel(@PathVariable planeModelId: Long) : ResponseEntity<*> {
        val result = planeModelService.fetchPlaneModelById(planeModelId)

        return result.fold(
            success = { ResponseEntity.ok(result.value) },
            failure = { r -> r.toProblem() }
        )
    }
}