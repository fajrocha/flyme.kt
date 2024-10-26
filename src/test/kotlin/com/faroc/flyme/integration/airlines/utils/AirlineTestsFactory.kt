package com.faroc.flyme.integration.airlines.utils

import com.faroc.flyme.airlines.api.requests.AddAirlineRequest

class AirlineTestsFactory {
    companion object {
        fun createAddRequest(name: String = "Bryanair", country: String = "Iceland") : AddAirlineRequest {
            return AddAirlineRequest(name, country)
        }
    }
}