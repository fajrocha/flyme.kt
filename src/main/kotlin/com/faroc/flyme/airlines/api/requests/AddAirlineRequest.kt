package com.faroc.flyme.airlines.api.requests

import com.faroc.flyme.airlines.domain.Airline
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddAirlineRequest(
    @field:NotBlank(message = "Name must not be empty or omitted.")
    @field:Size(message = "Name must not be between 1 and 50 characters.", min = 1, max = 50)
    val name: String,
    @field:NotBlank(message = "Country must not be empty or omitted.")
    @field:Size(message = "Country must not be between 1 and 50 characters.", min = 1, max = 50)
    val country: String,
)

fun AddAirlineRequest.toDomain() : Airline {
    return Airline(this.name, this.country)
}

