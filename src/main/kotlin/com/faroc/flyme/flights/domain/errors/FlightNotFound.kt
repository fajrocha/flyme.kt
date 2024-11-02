package com.faroc.flyme.flights.domain.errors

class FlightNotFound {
    companion object {
        const val DESCRIPTION = "The flight on request was not found."
        const val CODE = "Flight.NotFound"
    }
}