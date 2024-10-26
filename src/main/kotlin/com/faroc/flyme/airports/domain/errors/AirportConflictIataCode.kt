package com.faroc.flyme.airports.domain.errors

class AirportConflictIataCode {
    companion object {
        const val DESCRIPTION = "IATA code already exists."
        const val CODE = "Airport.Iata.Conflict"
    }
}