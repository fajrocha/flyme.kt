package com.faroc.flyme.integration.flights

import com.faroc.flyme.PostgresConfiguration
import com.faroc.flyme.airlines.infrastructure.AirlineRepository
import com.faroc.flyme.airports.infrastructure.AirportRepository
import com.faroc.flyme.integration.planes.ADD_PLANE_MODEL_URI
import com.faroc.flyme.integration.planes.ADD_PLANE_URI
import com.faroc.flyme.planes.api.requests.PlaneModelRequest
import com.faroc.flyme.planes.api.requests.PlaneRequest
import com.faroc.flyme.planes.api.responses.PlaneModelResponse
import com.faroc.flyme.planes.api.responses.PlaneResponse
import com.faroc.flyme.planes.infrastructure.PlaneModelRepository
import com.faroc.flyme.planes.infrastructure.PlaneRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

const val SCHEDULE_FLIGHT_URI = "v1/flights"

@OptIn(ExperimentalStdlibApi::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresConfiguration::class)
class FlightsTests(
    private val planeModelRepository: PlaneModelRepository,
    private val planeRepository: PlaneRepository,
    private val airlineRepository: AirlineRepository,
    private val airportRepository: AirportRepository,
    private val client: WebTestClient,
    ) {

    @BeforeEach
    fun clearDatabase() {
        runBlocking {
            airportRepository.deleteAll()
            airlineRepository.deleteAll()
            planeRepository.deleteAll()
            planeModelRepository.deleteAll()
        }
    }

    private fun addPlane(requestBody: PlaneRequest) : EntityExchangeResult<PlaneResponse> {
        return client.post()
            .uri(ADD_PLANE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<PlaneResponse>()
            .returnResult()
    }

    private fun addPlaneModel(requestBody: PlaneModelRequest) : EntityExchangeResult<PlaneModelResponse> {
        return client.post()
            .uri(ADD_PLANE_MODEL_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<PlaneModelResponse>()
            .returnResult()
    }


}