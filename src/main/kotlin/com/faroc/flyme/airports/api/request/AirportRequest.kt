package com.faroc.flyme.airports.api.request

import com.faroc.flyme.airports.domain.Airport
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AirportRequest(
    @field:NotBlank(message = "IATA code should not be blank or omitted.")
    @field:Size(message = "IATA code must be between 1 and 50 characters.", min = 1, max = 50)
    val iataCode: String,
    @field:NotBlank(message = "Name should not be blank or omitted.")
    @field:Size(message = "Name must be between 1 and 50 characters.", min = 1, max = 50)
    val name: String,
    @field:NotBlank(message = "City should not be blank or omitted.")
    @field:Size(message = "City must be between 1 and 50 characters.", min = 1, max = 50)
    val city: String,
    @field:NotBlank(message = "Country should not be blank or omitted.")
    @field:Size(message = "Country must be between 1 and 50 characters.", min = 1, max = 50)
    val country: String,
)

fun AirportRequest.toDomain() : Airport {
    return Airport.create(this.iataCode, this.name, this.city, this.country)
}
