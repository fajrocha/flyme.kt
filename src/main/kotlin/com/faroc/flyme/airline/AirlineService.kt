package com.faroc.flyme.airline

import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class AirlineService(
    private val airlineRepository: AirlineRepository) {

    suspend fun addAirlines(airlines: List<AddAirlineRequest>): List<AirlinesResponse> {
        val airlinesToAdd = airlines.map {
            (name, country) -> Airline(name = name, country = country)
        }

        val airlinesAdded = airlineRepository.saveAll(airlinesToAdd).toList()

        return airlinesAdded.map {
            (id, name, country) -> AirlinesResponse(id!!, name, country)
        }
    }

    suspend fun fetchAirlines(): List<AirlinesResponse> {
        val airlines = airlineRepository.findAll().toList()

        return airlines.map {
                (id, name, country) -> AirlinesResponse(id!!, name, country)
        }
    }
}