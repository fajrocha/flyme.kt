package com.faroc.flyme.flights.api.responses

import java.time.ZonedDateTime

data class ScheduleFlightResponse(
    val id: Long,
    val airportDeparture: Long,
    val airportArrival: Long,
    val plane: Long,
    val airline: Long,
    val duration: String,
    val departureTime: ZonedDateTime,
    val arrivalTime: ZonedDateTime,
)
