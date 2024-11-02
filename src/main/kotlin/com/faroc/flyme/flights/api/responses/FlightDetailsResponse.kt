package com.faroc.flyme.flights.api.responses

import java.time.ZonedDateTime

data class FlightDetailsResponse(
    val id: Long,
    val airline: FlightAirlineDetailsResponse,
    val departure: FlightAirportDetailsResponse,
    val arrival: FlightAirportDetailsResponse,
    val plane: FlightPlaneDetailsResponse,
    val duration: String,
)

data class FlightAirlineDetailsResponse(
    val name: String,
)

data class FlightAirportDetailsResponse(
    val iataCode: String,
    val city: String,
    val timeZone: String,
    val time: ZonedDateTime,
)

data class FlightPlaneDetailsResponse(
    val modelName: String,
)
