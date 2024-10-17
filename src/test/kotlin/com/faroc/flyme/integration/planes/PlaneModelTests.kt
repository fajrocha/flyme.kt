package com.faroc.flyme.integration.planes

import com.faroc.flyme.TestcontainersConfiguration
import com.faroc.flyme.planes.api.requests.PlaneModelRequest
import com.faroc.flyme.planes.api.responses.PlaneModelResponse
import com.faroc.flyme.planes.infrastructure.PlaneModelRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContainAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
class PlaneModelTests(
    @Autowired
    val client: WebTestClient,
    @Autowired
    val repository: PlaneModelRepository) {
    @BeforeEach
    fun clearDatabase() {
        runBlocking {
            repository.deleteAll()
        }
    }

    @Test
    fun `when adding plane models to platform should add plane models`() {
        runBlocking {
            // given:
            val requestBody = getTwoPlaneModels()
            val requestNames = requestBody.map { rb -> rb.name }
            val requestSeats = requestBody.map { rb -> rb.seats }

            // when:
            val requestResult = client.post()
                .uri("v1/plane-model")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .exchange()
                .expectStatus().isCreated
                .expectBodyList<PlaneModelResponse>()
                .returnResult()

            // then:
            val responseBody = requestResult.responseBody ?: listOf()

            responseBody.size shouldBeEqualTo requestBody.size
            repository.count() shouldBeEqualTo responseBody.size.toLong()

            val ids = responseBody.map { rb -> rb.id }
            ids.forEach {
                id -> repository.existsById(id).shouldBeTrue()
            }

            val responseNames = responseBody.map { rb -> rb.name }
            responseNames shouldContainAll requestNames

            val responseSeats = responseBody.map { rb -> rb.seats }
            responseSeats shouldContainAll requestSeats
        }
    }

    private fun getTwoPlaneModels() : List<PlaneModelRequest> {
        return listOf(
            PlaneModelRequest("Big Plane", 70),
            PlaneModelRequest("Small Plane", 20),
        )
    }

    private fun getPlaneModel() : PlaneModelRequest {
        return PlaneModelRequest("Big Plane", 70)
    }
}