package com.faroc.flyme.airports.api.request

import com.faroc.flyme.airports.domain.Airport

data class AirportRequest(
    val iataCode: String,
    val name: String,
    val city: String,
    val country: String,
)

fun AirportRequest.toDomain() : Airport {
    return Airport.create(this.iataCode, this.name, this.city, this.country)
}
