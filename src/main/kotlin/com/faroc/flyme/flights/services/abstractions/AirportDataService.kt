package com.faroc.flyme.flights.services.abstractions

import com.faroc.flyme.common.infrastructure.airportgap.responses.AirportData
import com.faroc.flyme.common.infrastructure.airportgap.responses.AirportsDistanceData

interface AirportDataService {
    suspend fun fetchAirportData(iataCode: String) : AirportData
    suspend fun fetchDistanceBetweenAirports(departureAirport: String, arrivalAirport: String) : AirportsDistanceData
}