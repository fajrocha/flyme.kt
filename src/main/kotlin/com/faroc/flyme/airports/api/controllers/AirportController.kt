package com.faroc.flyme.airports.api.controllers

import com.faroc.flyme.airports.api.request.AirportRequest
import com.faroc.flyme.airports.api.response.AirportResponse
import com.faroc.flyme.airports.services.AirportService
import com.faroc.flyme.common.api.errors.toProblem
import com.github.michaelbull.result.fold
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/airports")
class AirportController(private val airportService: AirportService) {

    @PostMapping
    suspend fun addAirport(@Valid @RequestBody airportRequest: AirportRequest) : ResponseEntity<*> {
        val result = airportService.addAirport(airportRequest)

        return result.fold(
            success = { response -> ResponseEntity(response, HttpStatus.CREATED)},
            failure = { err -> err.toProblem() }
        )
    }

    @GetMapping("{id}")
    suspend fun fetchAirport(@PathVariable id: Long) : ResponseEntity<*> {
        val result = airportService.fetchAirport(id)

        return result.fold(
            success = { response -> ResponseEntity.ok(response)},
            failure = { err -> err.toProblem() }
        )
    }

    @GetMapping
    suspend fun fetchAirports() : List<AirportResponse> {
        return airportService.fetchAirports()
    }
}