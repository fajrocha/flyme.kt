package com.faroc.flyme.flights.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.ZonedDateTime
import kotlin.time.Duration

@Table("flight")
data class Flight(
    @Column("airport_departure_id")
    val airportDeparture: Long,
    @Column("airport_arrival_id")
    val airportArrival: Long,
    @Column("plane_id")
    val plane: Long,
    @Column("airline_id")
    val airline: Long,
    @Column("duration")
    val duration: Duration,
    @Column("departure_time")
    val departureTime: ZonedDateTime,
    @Column("arrival_time")
    val arrivalTime: ZonedDateTime,
    @Id
    @Column("flight_id")
    val id: Long? = null,
)
