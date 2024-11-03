package com.faroc.flyme.flights.domain.errors

class FlightArrivalAirportNotFound {
    companion object {
        const val DESCRIPTION = "The arrival airport of the flight was not found."
        const val CODE = "Flight.ArrivalAirportNotFound"
    }
}