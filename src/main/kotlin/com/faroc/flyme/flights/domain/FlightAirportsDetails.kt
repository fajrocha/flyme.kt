package com.faroc.flyme.flights.domain

data class FlightAirportsDetails(
    val distance: Double,
    val departureAirportDetails: FlightAirportDetails,
    val arrivalAirportDetails: FlightAirportDetails,
)

data class FlightAirportDetails(
    val timezone: String,
)
