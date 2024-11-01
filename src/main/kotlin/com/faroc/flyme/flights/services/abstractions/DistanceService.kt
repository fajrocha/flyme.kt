package com.faroc.flyme.flights.services.abstractions

import com.faroc.flyme.flights.infrastructure.airportgap.responses.AirportsDataReport

interface DistanceService {
    suspend fun fetchDistanceBetweenAirports(departureAirport: String, arrivalAirport: String) : AirportsDataReport
}