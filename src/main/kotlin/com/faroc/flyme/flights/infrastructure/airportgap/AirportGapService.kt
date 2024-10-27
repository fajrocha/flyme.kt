package com.faroc.flyme.flights.infrastructure.airportgap

import com.faroc.flyme.flights.infrastructure.airportgap.config.AirportGapConfiguration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.server.ResponseStatusException

private const val DISTANCE_URI = "airports/distance"

@Service
class AirportGapService(
    private val configuration: AirportGapConfiguration,
) {
    suspend fun fetchDistanceBetweenAirports(
        departureAirport: String,
        arrivalAirport: String
    ) : AirportDistanceDetails {
        val webClient = WebClient.builder().baseUrl(configuration.apiUrl).build()

        val airportDistanceReport = webClient.post()
            .uri{ u ->
                u.path(DISTANCE_URI)
                    .queryParam("from", departureAirport)
                    .queryParam("to", arrivalAirport)
                    .build()
            }
            .headers { configuration.buildHeaders() }
            .retrieve()
            .onStatus({ status -> status.is5xxServerError }) { response ->
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch airport distance data.")
            }
            .onStatus({ status -> status.is5xxServerError }) { response ->
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch airport distance data.")
            }
            .awaitBody<AirportsDistanceResponse>()

        return airportDistanceReport.data?.attributes?.toAirportDistance()
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected response from airport distance source.")
    }
}

private fun AirportGapConfiguration.buildHeaders() : HttpHeaders {
    val headers = HttpHeaders()
    headers["Authorization"] = "Bearer ${this.apiToken}"

    return headers
}