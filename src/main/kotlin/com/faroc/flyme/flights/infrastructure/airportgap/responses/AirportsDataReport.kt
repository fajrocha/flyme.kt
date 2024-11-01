package com.faroc.flyme.flights.infrastructure.airportgap.responses

import com.faroc.flyme.flights.domain.FlightAirportDetails
import com.faroc.flyme.flights.domain.FlightAirportsDetails
import com.fasterxml.jackson.annotation.JsonProperty

data class AirportsDataReport(
    @JsonProperty("data")
    val data: AirportsDistanceData,
)

data class AirportsDistanceData(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("attributes")
    val attributes: Attributes,
)

data class Attributes(
    @JsonProperty("kilometers")
    val kilometers: Double,
    @JsonProperty("miles")
    val miles: Double,
    @JsonProperty("nautical_miles")
    val nauticalMiles: Double,
    @JsonProperty("from_airport")
    val fromAirport: FromAirport,
    @JsonProperty("to_airport")
    val toAirport: ToAirport,
)

data class FromAirport(
    @JsonProperty("timezone")
    val timezone: String,
)

data class ToAirport(
    @JsonProperty("timezone")
    val timezone: String
)

fun Attributes.toFlightAirportsDetails(): FlightAirportsDetails {
    return FlightAirportsDetails(
        this.kilometers,
        FlightAirportDetails(this.fromAirport.timezone),
        FlightAirportDetails(this.toAirport.timezone),
    )
}