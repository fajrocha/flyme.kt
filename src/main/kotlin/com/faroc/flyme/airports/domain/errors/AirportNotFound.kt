package com.faroc.flyme.airports.domain.errors

class AirportNotFound {
    companion object {
        const val DESCRIPTION = "Airport on request was not found."
        const val CODE = "Airport.NotFound"
    }
}