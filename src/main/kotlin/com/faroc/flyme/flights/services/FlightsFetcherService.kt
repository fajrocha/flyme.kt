package com.faroc.flyme.flights.services

import com.faroc.flyme.common.api.errors.Error
import com.faroc.flyme.common.api.errors.NotFoundError
import com.faroc.flyme.flights.api.responses.FlightDetailsResponse
import com.faroc.flyme.flights.domain.errors.FlightNotFound
import com.faroc.flyme.flights.infrastructure.repositories.FlightRepository
import com.faroc.flyme.flights.services.views.toResponse
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Service

@Service
class FlightsFetcherService(private val flightRepository: FlightRepository) {
    suspend fun fetchFlightById(flightId: Long) : Result<FlightDetailsResponse, Error> {
        val flightFetched = flightRepository.findByIdWithFlightDetails(flightId)
            ?: return Err(NotFoundError(FlightNotFound.DESCRIPTION, FlightNotFound.CODE))

        return Ok(flightFetched.toResponse())
    }
}