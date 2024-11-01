package com.faroc.flyme.flights.api.requests

import java.time.LocalDateTime

data class ScheduleFlightRequest(
    val departureIataCode: String,
    val arrivalIataCode: String,
    val plane: Long,
    val airlineName: String,
    val departureTime: LocalDateTime,
)
