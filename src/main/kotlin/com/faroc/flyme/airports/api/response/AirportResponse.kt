package com.faroc.flyme.airports.api.response

data class AirportResponse(
    val id: Long,
    val iataCode: String,
    val name: String,
    val city: String,
    val country: String,
)
