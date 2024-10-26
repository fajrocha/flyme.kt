package com.faroc.flyme.airports.services

import com.faroc.flyme.airports.api.request.AirportRequest
import com.faroc.flyme.airports.api.request.toDomain
import com.faroc.flyme.airports.api.response.AirportResponse
import com.faroc.flyme.airports.domain.errors.AirportConflictIataCode
import com.faroc.flyme.airports.domain.errors.AirportNotFound
import com.faroc.flyme.airports.domain.toResponse
import com.faroc.flyme.airports.infrastructure.AirportRepository
import com.faroc.flyme.common.api.errors.ConflictError
import com.faroc.flyme.common.api.errors.Error
import com.faroc.flyme.common.api.errors.NotFoundError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jakarta.transaction.Transactional
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class AirportService(private val airportRepository: AirportRepository) {

    @Transactional
    suspend fun addAirport(airportRequest: AirportRequest) : Result<AirportResponse, Error> {
        if (airportRepository.existsByIataCode(airportRequest.iataCode))
            return Err(ConflictError(AirportConflictIataCode.DESCRIPTION, AirportConflictIataCode.CODE))

        val airportAdded = airportRepository.save(airportRequest.toDomain())

        return Ok(airportAdded.toResponse())
    }

    @Transactional
    suspend fun fetchAirport(id: Long) : Result<AirportResponse, Error> {
        val airportFetched = airportRepository.findById(id)
            ?: return Err(NotFoundError(AirportNotFound.DESCRIPTION, AirportNotFound.CODE))

        return Ok(airportFetched.toResponse())
    }

    @Transactional
    suspend fun fetchAirports() : List<AirportResponse> {
        return airportRepository
            .findAll()
            .map { airport -> airport.toResponse() }
            .toList()
    }
}