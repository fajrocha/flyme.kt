package com.faroc.flyme.airlines.services

import com.faroc.flyme.airlines.domain.errors.AirlineNotFound
import com.faroc.flyme.airlines.infrastructure.AirlineRepository
import com.faroc.flyme.airlines.api.requests.AddAirlineRequest
import com.faroc.flyme.airlines.api.responses.AirlinesResponse
import com.faroc.flyme.airlines.domain.Airline
import com.faroc.flyme.common.api.errors.Error
import com.faroc.flyme.common.api.errors.NotFoundError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class AirlineService(
    private val airlineRepository: AirlineRepository
) {
    suspend fun addAirline(airline: AddAirlineRequest): AirlinesResponse {
        val (name, country) = airline

        val airlinesToAdd = Airline(name, country)

        val airlineAdded = airlineRepository.save(airlinesToAdd)

        return AirlinesResponse(airlineAdded.id!!, airlineAdded.name, airlineAdded.country)
    }

    suspend fun fetchAirlines(): List<AirlinesResponse> {
        val airlines = airlineRepository.findAll().toList()

        return airlines.map { (name, country,id) -> AirlinesResponse(id!!, name, country) }
    }

    suspend fun fetchAirline(airlineId : Long): Result<AirlinesResponse, Error> {
        val (name, country, id) = airlineRepository.findById(airlineId)
            ?: return Err(NotFoundError(AirlineNotFound.DESCRIPTION, AirlineNotFound.CODE))

        return Ok(AirlinesResponse(id!!, name, country))
    }
}