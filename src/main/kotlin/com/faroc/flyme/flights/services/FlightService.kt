package com.faroc.flyme.flights.services

import com.faroc.flyme.airports.infrastructure.AirportRepository
import com.faroc.flyme.common.api.errors.NotFoundError
import com.faroc.flyme.flights.api.requests.ScheduleFlightRequest
import com.faroc.flyme.flights.domain.errors.FlightDepartureAirportNotFound
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Service

@Service
class FlightService(
    private val airportRepository: AirportRepository
) {

    suspend fun scheduleFlight(scheduleFlightRequest: ScheduleFlightRequest) : Result<Boolean,Error> {
        val departureIataCode = scheduleFlightRequest.departureIataCode

        val airport = airportRepository.findByIataCode(departureIataCode)
            ?: Err(NotFoundError(FlightDepartureAirportNotFound.DESCRIPTION, FlightDepartureAirportNotFound.CODE))

        return Ok(true)
    }
}