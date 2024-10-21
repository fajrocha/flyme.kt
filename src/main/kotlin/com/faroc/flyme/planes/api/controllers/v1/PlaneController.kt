package com.faroc.flyme.planes.api.controllers.v1

import com.faroc.flyme.planes.api.requests.PlaneRequest
import com.faroc.flyme.planes.api.responses.PlaneResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("Plane Controller V1")
@RequestMapping("v1/planes")
@Tag(name = "Planes")
class PlaneController(
) {
    @PostMapping
    suspend fun addPlane(
        @Valid @RequestBody planeRequest: PlaneRequest) : ResponseEntity<PlaneResponse> {
        val planeAdded = PlaneResponse(1L, planeRequest.planeModel)

        return ResponseEntity(planeAdded, HttpStatus.CREATED)
    }
}