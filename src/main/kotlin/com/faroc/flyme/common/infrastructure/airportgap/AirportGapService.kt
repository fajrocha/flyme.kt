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

private const val DISTANCE_URI = "airports/distance"
private const val AIRPORT_URI = "airports"

@Service
class AirportGapService(private val configuration: AirportGapConfiguration) : AirportDataService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override suspend fun fetchAirportData(
        iataCode: String ) : AirportData {

        val webClient = WebClient.builder().baseUrl(configuration.apiUrl).build()

        val airportDistanceReport = webClient.get()
            .uri{ u -> u.pathSegment(AIRPORT_URI, iataCode).build() }
            .headers { configuration.buildHeaders() }
            .retrieve()
            .onStatus({ status -> status.is5xxServerError }) { _ ->
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch airport data.")
            }
            .onStatus({ status -> status.is5xxServerError }) { _ ->
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch airport data.")
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
            .onStatus({ status -> status.is5xxServerError }) { _ ->
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
}

private fun AirportGapConfiguration.buildHeaders() : HttpHeaders {
    val headers = HttpHeaders()
    headers["Authorization"] = "Bearer ${this.apiToken}"

    return headers
}