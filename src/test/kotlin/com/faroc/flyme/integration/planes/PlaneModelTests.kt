package com.faroc.flyme.integration.planes

import com.faroc.flyme.common.api.middleware.ValidationProblem
import com.faroc.flyme.configurations.PostgresConfiguration
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

const val FETCH_PLANE_MODELS_URI = "v1/plane-model"

@OptIn(ExperimentalStdlibApi::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import( PostgresConfiguration::class)
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
            val planeModelAdded = PlaneModelTestsClient(client)
                .addPlaneModelOk(addPlaneModelRequest)
                .responseBody
                ?: throw AssertionError("Response body when adding plane model should not be null.")

            // then:
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
            val addPlaneModelRequest = PlaneModelRequestFactory.create("", seats)

            // when:
            val requestResult = PlaneModelTestsClient(client)
                .addPlaneModel400(addPlaneModelRequest)
                .responseBody
                ?: throw AssertionError("Response body when adding plane model should not be null.")

            // then:
            val responseBody = requestResult

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
            val planeModelAdded = PlaneModelTestsClient(client)
                .addPlaneModelOk(requestBody)
                .responseBody
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

            val addedPlaneModel = PlaneModelTestsClient(client)
                .addPlaneModelOk(addPlaneModelRequest)
                .responseBody
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

    private fun fetchPlaneModel(id: Long) : String {
        return "$FETCH_PLANE_MODELS_URI/$id"
    }
}

class PlaneModelTestsClient(private val client: WebTestClient) {
    companion object {
        const val ADD_PLANE_MODEL_URI = "v1/plane-model"
    }

    fun addPlaneModelOk(requestBody: PlaneModelRequest) : EntityExchangeResult<PlaneModelResponse> {
        return client.post()
            .uri(ADD_PLANE_MODEL_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<PlaneModelResponse>()
            .returnResult()
    }

    fun addPlaneModel400(requestBody: PlaneModelRequest) : EntityExchangeResult<ValidationProblem> {
        return client.post()
            .uri(ADD_PLANE_MODEL_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<ValidationProblem>()
            .returnResult()
    }
}