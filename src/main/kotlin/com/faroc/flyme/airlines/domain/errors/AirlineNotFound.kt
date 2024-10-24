package com.faroc.flyme.airlines.domain.errors

class AirlineNotFound {
    companion object {
        const val DESCRIPTION = "Airline requested was not found."
        const val CODE = "Airline.NotFound"
    }
}