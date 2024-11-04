package com.faroc.flyme.integration.airlines.utils

import com.faroc.flyme.airlines.api.requests.AddAirlineRequest

class AirlineTestsFactory {
    companion object {
        fun createAddRequest(name: String = "United Airlines", country: String = "USA") : AddAirlineRequest {
            return AddAirlineRequest(name, country)
        }
    }
}