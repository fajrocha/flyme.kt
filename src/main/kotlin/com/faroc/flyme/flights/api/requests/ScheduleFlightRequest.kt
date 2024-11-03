package com.faroc.flyme.flights.api.requests

import jakarta.validation.constraints.*
import java.time.LocalDateTime

data class ScheduleFlightRequest(
    @field:NotBlank(message = "Departure IATA should not be blank or omitted.")
    @field:Size(message = "Departure IATA must be between 1 and 3 characters.", min = 1, max = 3)
    val departureIataCode: String,
    @field:NotBlank(message = "Arrival IATA should not be blank or omitted.")
    @field:Size(message = "Arrival IATA must be between 1 and 3 characters.", min = 1, max = 3)
    val arrivalIataCode: String,
    @field:NotNull(message = "Plane id should not be omitted.")
    @field:Min(message = "Plane id must be higher than 0.", value = 1)
    @field:Max(message = "Plane id must be lower than 9223372036854775807.", value = Long.MAX_VALUE)
    val planeId: Long,
    @field:NotBlank(message = "Airline name should not be blank or omitted.")
    @field:Size(message = "Airline name must be between 1 and 50 characters.", min = 1, max = 50)
    val airlineName: String,
    val departureTime: LocalDateTime,
)
