package com.faroc.flyme.integration.flights.utils

import com.faroc.flyme.flights.api.requests.ScheduleFlightRequest
import java.time.LocalDateTime

class FlightTestsFactory {
    companion object {
        fun createScheduleFlightRequest(
            departureIata: String,
            arrivalIata: String,
            planeId: Long,
            airlineName: String,
            departureTime: LocalDateTime,
            ) : ScheduleFlightRequest {
            return ScheduleFlightRequest(
                departureIata,
                arrivalIata,
                planeId,
                airlineName,
                departureTime,
            )
        }
    }
}