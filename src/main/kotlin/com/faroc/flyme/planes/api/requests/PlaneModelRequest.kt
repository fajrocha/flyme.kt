package com.faroc.flyme.planes.api.requests

import com.faroc.flyme.planes.domain.PlaneModel
import jakarta.validation.constraints.*

data class PlaneModelRequest(
    @field:NotBlank(message = "Name should not be blank or omitted.")
    @field:Size(message = "Name must be between 1 and 50 characters.", min = 1, max = 50)
    val name: String,
    @field:NotNull(message = "Name should not be omitted.")
    @field:Min(message = "Seats must be higher than 0.", value = 1)
    @field:Max(message = "Seats must be lower than 2000.", value = 2000)
    val seats: Short
)

fun PlaneModelRequest.toDomain() : PlaneModel {
    return PlaneModel(this.name, this.seats)
}