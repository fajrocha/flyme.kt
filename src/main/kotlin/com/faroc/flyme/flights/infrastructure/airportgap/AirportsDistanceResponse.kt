package com.faroc.flyme.flights.infrastructure.airportgap

import com.fasterxml.jackson.annotation.JsonProperty

data class AirportsDistanceResponse(
    @JsonProperty("data")
    val data: AirportsDistanceData?,
)

data class AirportsDistanceData(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("attributes")
    val attributes: Attributes?,
)

data class Attributes(
    @JsonProperty("kilometers")
    val kilometers: Double,
    @JsonProperty("miles")
    val miles: Double,
    @JsonProperty("nautical_miles")
    val nauticalMiles: Double,
)

fun Attributes.toAirportDistance() : AirportDistanceDetails {
    return AirportDistanceDetails(this.kilometers, this.miles, this.nauticalMiles)
}