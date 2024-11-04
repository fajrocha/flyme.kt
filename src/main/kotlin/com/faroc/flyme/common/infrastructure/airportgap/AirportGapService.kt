package com.faroc.flyme.common.infrastructure.airportgap

import com.faroc.flyme.common.infrastructure.airportgap.config.AirportGapConfiguration
import com.faroc.flyme.common.infrastructure.airportgap.responses.AirportData
import com.faroc.flyme.common.infrastructure.airportgap.responses.AirportsDistanceData
import com.faroc.flyme.flights.services.abstractions.AirportDataService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder

@Service
class AirportGapService(private val configuration: AirportGapConfiguration) : AirportDataService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        private const val DISTANCE_URI = "/airports/distance"
        private const val AIRPORT_URI = "airports"
        const val AIRPORT_ERROR_DESCRIPTION = "Failed to fetch airport data."
        const val AIRPORTS_DISTANCE_ERROR_DESCRIPTION = "Failed to fetch airports distance data."
    }

    override suspend fun fetchAirportData(
        iataCode: String ) : AirportData {

        val webClient = buildClient()

        val airportDistanceReport = webClient.get()
            .uri{ u -> u.pathSegment(AIRPORT_URI, iataCode).build() }
            .retrieve()
            .onStatus({ status -> status.is4xxClientError }) { _ ->
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, AIRPORT_ERROR_DESCRIPTION)
            }
            .onStatus({ status -> status.is5xxServerError }) { _ ->
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, AIRPORT_ERROR_DESCRIPTION)
            }
            .bodyToFlow<AirportData>()
            .catch { ex ->
                log.error("Failed to process the API response.", ex)
            }
            .firstOrNull()

        return airportDistanceReport ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch airport data.")
    }

    override suspend fun fetchDistanceBetweenAirports(
        departureAirport: String,
        arrivalAirport: String
    ) : AirportsDistanceData {

        val webClient = buildClient()

        val airportDistanceReport = webClient.post()
            .uri{ u ->
                u.path(DISTANCE_URI)
                    .queryParam("from", departureAirport)
                    .queryParam("to", arrivalAirport)
                    .build()
            }
            .retrieve()
            .onStatus({ status -> status.is4xxClientError }) { ex ->
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch airports distance data.")
            }
            .onStatus({ status -> status.is5xxServerError }) { _ ->
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch airports distance data.")
            }
            .bodyToFlow<AirportsDistanceData>()
            .catch { ex ->
                log.error("Failed to process the API response when fetching airports distance.", ex)
            }
            .firstOrNull()

        return airportDistanceReport
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch airports distance data.")
    }

    private fun buildClient() : WebClient {
        val baseUrl = UriComponentsBuilder
            .fromHttpUrl(configuration.apiUrl)
            .port(configuration.port)
            .build()
            .toUriString()

        return WebClient.builder()
            .defaultHeaders {
                configuration.buildHeaders()
            }
            .baseUrl(baseUrl)
            .build()
    }
}

private fun AirportGapConfiguration.buildHeaders() : HttpHeaders {
    val headers = HttpHeaders()
    headers["Authorization"] = "Bearer ${this.apiToken}"

    return headers
}

