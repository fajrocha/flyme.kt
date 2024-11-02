package com.faroc.flyme.common.infrastructure.airportgap.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class AirportsDistanceData(
    @JsonProperty("data")
    val data: AirportsDistanceDataReport,
)

data class AirportsDistanceDataReport(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("attributes")
    val attributes: AirportsDistanceAttributes,
)

data class AirportsDistanceAttributes(
    @JsonProperty("kilometers")
    val kilometers: Double,
    @JsonProperty("miles")
    val miles: Double,
    @JsonProperty("nautical_miles")
    val nauticalMiles: Double,
)