package com.faroc.flyme.airline.domain.errors

class AirlineNotFound {
    companion object {
        const val DESCRIPTION = "Airline request was not found."
        const val CODE = "Airline.NotFound"
    }
}