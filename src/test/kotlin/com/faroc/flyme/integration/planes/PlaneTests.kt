package com.faroc.flyme.integration.planes

import com.faroc.flyme.TestcontainersConfiguration
import com.faroc.flyme.common.api.middleware.ValidationProblem
import com.faroc.flyme.integration.planes.utils.PlaneModelRequestFactory
import com.faroc.flyme.integration.planes.utils.PlaneRequestFactory
import com.faroc.flyme.planes.api.requests.PlaneModelRequest
import com.faroc.flyme.planes.api.requests.PlaneRequest
import com.faroc.flyme.planes.api.responses.PlaneModelResponse
import com.faroc.flyme.planes.api.responses.PlaneResponse
import com.faroc.flyme.planes.domain.errors.PlaneModelNotFound
import com.faroc.flyme.planes.infrastructure.PlaneModelRepository
import com.faroc.flyme.planes.infrastructure.PlaneRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContainAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
class PlaneTests(
    @Autowired
    private val client: WebTestClient,
    @Autowired
    private val planeRepository: PlaneRepository,
    @Autowired
    private val planeModelRepository: PlaneModelRepository
) {
    @BeforeEach
    fun clearDatabase() {
        runBlocking {
            planeRepository.deleteAll()
            planeModelRepository.deleteAll()
        }
    }

    @Test
    fun `when adding plane but request is invalid should return bad request`() {
        runBlocking {
            val plane = PlaneRequestFactory.create("")

            val response = client.post()
                .uri("v1/planes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(plane))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ValidationProblem>()
                .returnResult()
                .responseBody
                ?: throw AssertionError("Plane response cannot be null.")

            response.detail shouldBeEqualTo ValidationProblem.DETAIL

            response.errors["planeModel"]?.shouldContainAll(
                arrayOf(
                    "Plane model should not be blank or omitted.",
                    "Plane model must be between 1 and 50 characters."
                )
            )
        }
    }

    @Test
    fun `when adding plane but plane model does not exist should return not found`() {
        runBlocking {
            val plane = PlaneRequestFactory.create("Airbus 320")

            val response = client.post()
                .uri("v1/planes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(plane))
                .exchange()
                .expectStatus().isNotFound
                .expectBody<ProblemDetail>()
                .returnResult()
                .responseBody
                ?: throw AssertionError("Plane response cannot be null.")

            response.detail shouldBeEqualTo PlaneModelNotFound.DESCRIPTION
        }
    }

    @Test
    fun `when adding plane should add plane to platform`() {
        runBlocking {
            val planeModel = PlaneModelRequestFactory.create()
            val planeModelAdded = addPlaneModel(planeModel).responseBody
                ?: throw AssertionError("Plane model response cannot be null.")

            val plane = PlaneRequestFactory.create(planeModel.name)

            val planeAdded = addPlane(plane).responseBody
                ?: throw AssertionError("Plane response cannot be null.")

            planeRepository.existsById(planeAdded.id).shouldBeTrue()
            planeModelAdded.shouldBeEqualTo(planeAdded.planeModel)
        }
    }

    @Test
    fun `when fetching existing plane should return plane`() {
        runBlocking {
            // given:
            val planeModel = PlaneModelRequestFactory.create()
            addPlaneModel(planeModel).responseBody
                ?: throw AssertionError("Plane model response cannot be null.")

            val plane = PlaneRequestFactory.create(planeModel.name)

            val planeAdded = addPlane(plane).responseBody
                ?: throw AssertionError("Plane response cannot be null.")

            val response = client.get()
                .uri("v1/planes/${planeAdded.id}")
                .exchange()
                .expectStatus().isOk
                .expectBody<PlaneResponse>()
                .returnResult()

            val planeFetched = response.responseBody
                ?: throw AssertionError("Plane fetched cannot be null.")

            planeFetched.id shouldBeEqualTo planeAdded.id
        }
    }

    private fun addPlane(requestBody: PlaneRequest) : EntityExchangeResult<PlaneResponse> {
        return client.post()
            .uri("v1/planes")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestBody))
            .exchange()
            .expectStatus().isCreated
            .expectBody<PlaneResponse>()
            .returnResult()
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