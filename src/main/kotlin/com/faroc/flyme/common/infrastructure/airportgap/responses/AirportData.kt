package com.faroc.flyme.common.infrastructure.airportgap.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class AirportData(
    @JsonProperty("data")
    val data: AirportDataReport,
)

data class AirportDataReport(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("attributes")
    val attributes: AirportAttributes,
)

data class AirportAttributes(
    @JsonProperty("timezone")
    val timezone: String,
)