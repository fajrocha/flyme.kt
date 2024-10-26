package com.faroc.flyme.integration.planes

import com.faroc.flyme.TestcontainersConfiguration
import com.faroc.flyme.common.api.middleware.ValidationProblem
import com.faroc.flyme.integration.planes.utils.PlaneModelRequestFactory
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

const val ADD_PLANE_MODEL_URI = "v1/plane-model"
const val FETCH_PLANE_MODELS_URI = "v1/plane-model"

@OptIn(ExperimentalStdlibApi::class)
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
    fun `when adding plane model to platform should add plane model`() {
        runBlocking {
            // given:
            val addPlaneModelRequest = PlaneModelRequestFactory.create()

            // when:
            val addPlaneModelResponse = addPlaneModel(addPlaneModelRequest)

            // then:
            val planeModelAdded = addPlaneModelResponse.responseBody
                ?: throw AssertionError("Response body should not be null when adding plane model.")

            addPlaneModelRequest.shouldBeEquivalentTo(planeModelAdded)
            repository.existsById(planeModelAdded.id).shouldBeTrue()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "-1, 'Seats must be higher than 0.'",
        "2001, 'Seats must be lower than 2000.'"
    )
    fun `when adding invalid plane model to platform should return bad request`(
        seats: Short,
        seatsValidationError: String,
    ) {
        runBlocking {
            // given:
            val requestBody = PlaneModelRequestFactory.create("", seats)

            // when:
            val requestResult = client.post()
                .uri(ADD_PLANE_MODEL_URI)
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

            responseBody.errors["name"]?.shouldContainAll(
                arrayOf(
                    "Name should not be blank or omitted.",
                    "Name must be between 1 and 50 characters.")
            )

            responseBody.errors["seats"]?.shouldContain(seatsValidationError)
        }
    }

    @Test
    fun `when fetching non existing plane model by id should return plane model`() {
        runBlocking {
            // given:
            val planeModelId = 1L

            // when:
            val requestResult = client.get()
                .uri(fetchPlaneModel(planeModelId))
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
            val requestBody = PlaneModelRequestFactory.create()
            val planeModelAddedResponse = addPlaneModel(requestBody)

            val planeModelAdded = planeModelAddedResponse.responseBody
                ?: throw AssertionError("Response body when adding plane model should not be null.")

            val planeModelId = planeModelAdded.id

            // when:
            val requestResult = client.get()
                .uri(fetchPlaneModel(planeModelId))
                .exchange()
                .expectStatus().isOk
                .expectBody<PlaneModelResponse>()
                .returnResult()

            // then:
            val planeModelFetched = requestResult.responseBody
                ?: throw AssertionError("Response body should not be null.")

            planeModelFetched.shouldBeEquivalentTo(planeModelAdded)
        }
    }

    @Test
    fun `when fetching plane models should return plane models`() {
        runBlocking {
            // given:
            val addPlaneModelRequest = PlaneModelRequestFactory.create()

            val addedPlaneModel = addPlaneModel(addPlaneModelRequest).responseBody
                ?: throw AssertionError("Response body when adding plane model should not be null.")
            val expectedPlaneModelsFetched = listOf(addedPlaneModel)

            // when:
            val requestResult = client.get()
                .uri(FETCH_PLANE_MODELS_URI)
                .exchange()
                .expectStatus().isOk
                .expectBodyList<PlaneModelResponse>()
                .returnResult()

            // then:
            val fetchedPlaneModels = requestResult.responseBody
                ?: throw AssertionError("Response body should not be null.")

            fetchedPlaneModels.shouldBeEquivalentTo(expectedPlaneModelsFetched)
        }
    }

    private fun addPlaneModel(requestBody: PlaneModelRequest) : EntityExchangeResult<PlaneModelResponse> {
        return client.post()
            .uri(ADD_PLANE_MODEL_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestBody))
            .exchange()
            .expectStatus().isCreated
            .expectBody<PlaneModelResponse>()
            .returnResult()
    }

    private fun fetchPlaneModel(id: Long) : String {
        return "$FETCH_PLANE_MODELS_URI/$id"
    }
}