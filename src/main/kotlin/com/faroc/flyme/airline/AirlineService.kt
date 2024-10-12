package com.faroc.flyme.airline

import com.faroc.flyme.airline.domain.Airline
import com.faroc.flyme.airline.domain.errors.AirlineNotFound
import com.faroc.flyme.airline.requests.AddAirlineRequest
import com.faroc.flyme.airline.responses.AirlinesResponse
import com.faroc.flyme.common.errors.Error
import com.faroc.flyme.common.errors.NotFoundError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class AirlineService(
    private val airlineRepository: AirlineRepository) {

    suspend fun addAirlines(airlines: List<AddAirlineRequest>): List<AirlinesResponse> {
        val airlinesToAdd = airlines.map {
            (name, country) -> Airline(name,country)
        }

        val airlinesAdded = airlineRepository.saveAll(airlinesToAdd).toList()

        return airlinesAdded.map {
            (country, name, id) -> AirlinesResponse(id!!, name, country)
        }
    }

    suspend fun fetchAirlines(): List<AirlinesResponse> {
        val airlines = airlineRepository.findAll().toList()

        return airlines.map {
                (name, country,id) -> AirlinesResponse(id!!, name, country)
        }
    }

    suspend fun fetchAirline(airlineId : Long): Result<AirlinesResponse, Error> {
        val (name, country, id) = airlineRepository.findById(airlineId)
            ?: return Err(NotFoundError(AirlineNotFound.DESCRIPTION, AirlineNotFound.CODE))

        return Ok(AirlinesResponse(id!!, name, country))
    }
}