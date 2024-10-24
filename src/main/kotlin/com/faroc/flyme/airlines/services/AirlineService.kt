package com.faroc.flyme.airlines.services

import com.faroc.flyme.airlines.api.requests.AddAirlineRequest
import com.faroc.flyme.airlines.api.requests.toDomain
import com.faroc.flyme.airlines.api.responses.AirlinesResponse
import com.faroc.flyme.airlines.domain.errors.AirlineNotFound
import com.faroc.flyme.airlines.domain.toResponse
import com.faroc.flyme.airlines.infrastructure.AirlineRepository
import com.faroc.flyme.common.api.errors.Error
import com.faroc.flyme.common.api.errors.NotFoundError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class AirlineService(
    private val airlineRepository: AirlineRepository
) {
    suspend fun addAirline(airline: AddAirlineRequest): AirlinesResponse {
        val airlineAdded = airlineRepository.save(airline.toDomain())

        return airlineAdded.toResponse()
    }

    suspend fun fetchAirlines(): List<AirlinesResponse> {
        return airlineRepository.findAll().map { airline -> airline.toResponse() }.toList()
    }

    suspend fun fetchAirline(airlineId : Long): Result<AirlinesResponse, Error> {
        val airlineFetched = airlineRepository.findById(airlineId)
            ?: return Err(NotFoundError(AirlineNotFound.DESCRIPTION, AirlineNotFound.CODE))

        return Ok(airlineFetched.toResponse())
    }
}