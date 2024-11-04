package com.faroc.flyme.flights.services.views

import com.faroc.flyme.common.domain.secondsToFormattedDuration
import com.faroc.flyme.flights.api.responses.FlightAirlineDetailsResponse
import com.faroc.flyme.flights.api.responses.FlightAirportDetailsResponse
import com.faroc.flyme.flights.api.responses.FlightDetailsResponse
import com.faroc.flyme.flights.api.responses.FlightPlaneDetailsResponse
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

data class FlightWithDetailsView(
    val id: Long,
    val airlineName: String,
    val departureIata: String,
    val departureCity: String,
    val departureTimeZone: String,
    val arrivalIata: String,
    val arrivalCity: String,
    val arrivalTimeZone: String,
    val planeModelName: String,
    val duration: Int,
    val departureTime: Instant,
    val arrivalTime: Instant,
) {
    val departureZonedTime: ZonedDateTime
        get() = ZonedDateTime.ofInstant(departureTime, ZoneId.of(departureTimeZone))

    val arrivalZonedTime : ZonedDateTime
        get() = ZonedDateTime.ofInstant(arrivalTime, ZoneId.of(arrivalTimeZone))
}

fun FlightWithDetailsView.toResponse(): FlightDetailsResponse {
    val departure = FlightAirportDetailsResponse(
        this.departureIata,
        this.departureCity,
        this.departureTimeZone,
        this.departureZonedTime
    )

    val arrival = FlightAirportDetailsResponse(
        this.arrivalIata,
        this.arrivalCity,
        this.arrivalTimeZone,
        this.arrivalZonedTime
    )

    return FlightDetailsResponse(
        this.id,
        FlightAirlineDetailsResponse(airlineName),
        departure,
        arrival,
        FlightPlaneDetailsResponse(this.planeModelName),
        this.duration.secondsToFormattedDuration()
    )
}
