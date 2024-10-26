package com.faroc.flyme.integration.airports.utils

import com.faroc.flyme.airports.api.request.AirportRequest

class AirportTestsFactory {
    companion object {
        fun createAddRequest(
                iataCode: String = "LAX",
                name: String = "Los Angeles International Airport",
                city: String = "Los Angeles",
                country: String = "USA") : AirportRequest {

            return AirportRequest(iataCode, name, city, country)
        }
    }
}
