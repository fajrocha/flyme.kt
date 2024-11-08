package com.faroc.flyme.planes.api.requests

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PlaneRequest(
    @JsonProperty("planeModel")
    @field:NotBlank(message = "Plane model should not be blank or omitted.")
    @field:Size(message = "Plane model must be between 1 and 50 characters.", min = 1, max = 50)
    val planeModel: String,
)
