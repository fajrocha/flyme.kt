package com.faroc.flyme.flights.domain

import com.faroc.flyme.common.domain.secondsToFormattedDuration
import com.faroc.flyme.flights.api.responses.ScheduleFlightResponse
import com.faroc.flyme.planes.services.views.FlightPlaneView
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

@Table("flight")
class Flight private constructor(
    @Column("airport_departure_id")
    val airportDeparture: Long,
    @Column("airport_arrival_id")
    val airportArrival: Long,
    @Column("plane_id")
    val plane: Long,
    @Column("airline_id")
    val airline: Long,
    @Column("duration")
    val duration: Int,
    @Column("departure_time")
    val departureTime: ZonedDateTime,
    @Column("arrival_time")
    val arrivalTime: ZonedDateTime,
    @Id
    @Column("flight_id")
    val id: Long? = null,
) {
    companion object {
        fun create(
            airportDeparture: Long,
            airportArrival: Long,
            plane: FlightPlaneView,
            airline: Long,
            departureTime: LocalDateTime,
            flightAirportsDetails: FlightAirportsDetails,
            id: Long? = null,) : Flight {

            val duration = flightAirportsDetails.distance.toFlightDistance(plane.avgSpeed)

            val zonedDepartureTime = ZonedDateTime.of(
                departureTime,
                ZoneId.of(flightAirportsDetails.departureAirportDetails.timezone)
            )

            val zonedArrivalTime = departureTime.calculateArrivalTime(
                duration,
                flightAirportsDetails.arrivalAirportDetails.timezone
            )

            return Flight(
                airportDeparture,
                airportArrival,
                plane.planeId,
                airline,
                duration.inWholeSeconds.toInt(),
                zonedDepartureTime,
                zonedArrivalTime,
                id,
            )
        }
    }
}

fun Flight.toResponse(): ScheduleFlightResponse {
    val duration = this.duration.secondsToFormattedDuration()

    return ScheduleFlightResponse(
        this.id!!,
        this.airportDeparture,
        this.airportDeparture,
        this.plane,
        this.airline,
        duration,
        this.departureTime,
        this.arrivalTime)
}

private fun Double.toFlightDistance(planeAvgSpeed : Double ): Duration {
    return this.div(planeAvgSpeed).toDuration(DurationUnit.HOURS)
}

private fun LocalDateTime.calculateArrivalTime(flightDuration: Duration, timeZone :String): ZonedDateTime {
    val arrivalTime = this.plus(flightDuration.toJavaDuration()).truncatedTo(ChronoUnit.SECONDS)

    return ZonedDateTime.of(
        arrivalTime,
        ZoneId.of(timeZone)
    )
}


