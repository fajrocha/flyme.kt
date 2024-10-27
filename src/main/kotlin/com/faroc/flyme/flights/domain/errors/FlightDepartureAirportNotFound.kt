package com.faroc.flyme.flights.domain.errors

class FlightDepartureAirportNotFound {
    companion object {
        const val DESCRIPTION = "The departure airport of the flight was not found."
        const val CODE = "Flight.DepartureAirportNotFound"
    }
}