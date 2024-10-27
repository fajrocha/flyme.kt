package com.faroc.flyme.flights.api.controllers

import com.faroc.flyme.flights.infrastructure.airportgap.AirportDistanceDetails
import com.faroc.flyme.flights.infrastructure.airportgap.AirportGapService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/flights")
class FlightsController(private val service: AirportGapService) {
    @GetMapping("deleteme")
    suspend fun fetchAirportDistance() : AirportDistanceDetails {
        return service.fetchDistanceBetweenAirports("LAX", "OPO")
    }
}