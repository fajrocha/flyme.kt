package com.faroc.flyme.integration.planes

import com.faroc.flyme.TestcontainersConfiguration
import com.faroc.flyme.common.api.middleware.ValidationProblem
import com.faroc.flyme.planes.api.requests.PlaneModelRequest
import com.faroc.flyme.planes.api.responses.PlaneModelResponse
import com.faroc.flyme.planes.domain.errors.PlaneModelNotFound
import com.faroc.flyme.planes.infrastructure.PlaneModelRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
class PlaneRequestModelTests(
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
            val requestBody = PlaneModelRequest("Airbus 320", 70)

            // when:
            val requestResult = addPlaneModel(requestBody)

            // then:
            val responseBody = requestResult.responseBody

            responseBody?.name shouldBeEqualTo requestBody.name
            responseBody?.seats shouldBeEqualTo requestBody.seats

            val planeModelAddedId = responseBody?.id ?: -1
            repository.existsById(planeModelAddedId).shouldBeTrue()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "-1, 'Seats must be higher than 0.'",
        "2001, 'Seats must be lower than 2000.'"
    )
    fun `when adding invalid plane model to platform should return bad request`(seats: Short, seatsValidationError: String) {
        runBlocking {
            // given:
            val requestBody = PlaneModelRequest("", 2001)

            // when:
            val requestResult = client.post()
                .uri("v1/plane-model")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ValidationProblem>()
                .returnResult()

            // then:
            val responseBody = requestResult.responseBody
                ?: throw AssertionError("Response body should not be null.")

            responseBody.detail shouldBeEqualTo ValidationProblem.DETAIL
            responseBody.errors.shouldNotBeEmpty()

            val nameKey = "name"
            responseBody.errors.containsKey(nameKey).shouldBeTrue()
            responseBody.errors[nameKey]?.shouldContainAll(
                arrayOf(
                    "Name should not be blank or omitted.",
                    "Name must be between 1 and 50 characters.")
            )

            val seatsKey = "seats"
            responseBody.errors.containsKey(seatsKey).shouldBeTrue()
            responseBody.errors[seatsKey]?.shouldContain("Seats must be lower than 2000.")
        }
    }

    @Test
    fun `when fetching plane models should return plane models`() {
        runBlocking {
            // given:
            val requestBody = PlaneModelRequest("Big Plane", 70)
            val expectedResponseBody = listOf(requestBody)

            addPlaneModel(requestBody)

            // when:
            val requestResult = client.get()
                .uri("v1/plane-model")
                .exchange()
                .expectStatus().isOk
                .expectBodyList<PlaneModelResponse>()
                .returnResult()

            // then:
            val responseBody = requestResult.responseBody
                ?: throw AssertionError("Response body should not be null.")

            responseBody.size shouldBeEqualTo expectedResponseBody.size
            responseBody.map { rb -> rb.name  } shouldContainAll expectedResponseBody.map { erb -> erb.name }
            responseBody.map { rb -> rb.seats  } shouldContainAll expectedResponseBody.map { erb -> erb.seats }
        }
    }

    @Test
    fun `when fetching non existing plane model by id should return plane model`() {
        runBlocking {
            // given:
            val planeModelId = 1L

            // when:
            val requestResult = client.get()
                .uri("v1/plane-model/$planeModelId")
                .exchange()
                .expectStatus().isNotFound
                .expectBody<ProblemDetail>()
                .returnResult()

            // then:
            val responseBody = requestResult.responseBody
                ?: throw AssertionError("Response body should not be null.")

            responseBody.detail shouldBeEqualTo PlaneModelNotFound.DESCRIPTION
        }
    }

    @Test
    fun `when fetching plane model by id should return plane model`() {
        runBlocking {
            // given:
            val requestBody = PlaneModelRequest("Big Plane", 70)
            val planeModelAddedResponse = addPlaneModel(requestBody)

            val planeModelAdded = planeModelAddedResponse.responseBody
                ?: throw AssertionError("Response body should not be null.")

            val planeModelId = planeModelAdded.id

            // when:
            val requestResult = client.get()
                .uri("v1/plane-model/$planeModelId")
                .exchange()
                .expectStatus().isOk
                .expectBody<PlaneModelResponse>()
                .returnResult()

            // then:
            val planeModelFetched = requestResult.responseBody
                ?: throw AssertionError("Response body should not be null.")

            planeModelFetched.shouldBeEqualTo(planeModelAdded)
        }
    }

    private fun addPlaneModel(requestBody: PlaneModelRequest) : EntityExchangeResult<PlaneModelResponse> {
        return client.post()
            .uri("v1/plane-model")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestBody))
            .exchange()
            .expectStatus().isCreated
            .expectBody<PlaneModelResponse>()
            .returnResult()
    }
}