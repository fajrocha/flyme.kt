package com.faroc.flyme.flights.domain.services

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TimezoneCalculator {
    fun getUtcDepartureTime() : Instant {
        val localDepartureTime = LocalDateTime.parse("2024-10-26T10:00")
        val departureZoneId = ZoneId.of("America/New_York")

        return ZonedDateTime.of(localDepartureTime, departureZoneId).toInstant()
    }
}