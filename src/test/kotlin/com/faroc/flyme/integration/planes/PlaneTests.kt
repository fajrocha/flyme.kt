package com.faroc.flyme.integration.planes

import com.faroc.flyme.common.api.middleware.ValidationProblem
import com.faroc.flyme.common.api.responses.PaginatedResponse
import com.faroc.flyme.configurations.PostgresConfiguration
import com.faroc.flyme.integration.planes.utils.PlaneModelTestsFactory
import com.faroc.flyme.integration.planes.utils.PlaneTestsFactory
import com.faroc.flyme.planes.api.requests.PlaneRequest
import com.faroc.flyme.planes.api.responses.PlaneResponse
import com.faroc.flyme.planes.domain.errors.PlaneModelNotFound
import com.faroc.flyme.planes.infrastructure.PlaneModelRepository
import com.faroc.flyme.planes.infrastructure.PlaneRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeEquivalentTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContainAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import kotlin.test.Test

const val FETCH_PLANES_URI = "v1/planes"

@OptIn(ExperimentalStdlibApi::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresConfiguration::class)
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
            val addPlaneRequest = PlaneTestsFactory.createAddRequest("")

            val response = PlaneTestsClient(client)
                .addPlaneBadRequest(addPlaneRequest)
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
            val addPlaneRequest = PlaneTestsFactory.createAddRequest("Airbus 320")

            val response = PlaneTestsClient(client)
                .addPlaneNotFound(addPlaneRequest)
                .responseBody
                ?: throw AssertionError("Plane response cannot be null.")

            response.detail shouldBeEqualTo PlaneModelNotFound.DESCRIPTION
        }
    }

    @Test
    fun `when adding plane should add plane to platform`() {
        runBlocking {
            val planeModel = PlaneModelTestsFactory.createAddRequest()
            val planeModelAdded = PlaneModelTestsClient(client)
                .addPlaneModelOk(planeModel)
                .responseBody
                ?: throw AssertionError("Plane model response cannot be null.")

            val plane = PlaneTestsFactory.createAddRequest(planeModel.name)

            val planeAdded = PlaneTestsClient(client)
                .addPlaneOk(plane)
                .responseBody
                ?: throw AssertionError("Plane response cannot be null.")

            planeRepository.existsById(planeAdded.id).shouldBeTrue()
            planeModelAdded.shouldBeEqualTo(planeAdded.planeModel)
        }
    }

    @Test
    fun `when fetching existing plane should return plane`() {
        runBlocking {
            // given:
            val planeModel = PlaneModelTestsFactory.createAddRequest()

            PlaneModelTestsClient(client).addPlaneModelOk(planeModel)

            val plane = PlaneTestsFactory.createAddRequest(planeModel.name)

            val planeAdded = PlaneTestsClient(client)
                .addPlaneOk(plane)
                .responseBody
                ?: throw AssertionError("Plane response cannot be null.")

            val response = client.get()
                .uri("v1/planes/${planeAdded.id}")
                .exchange()
                .expectStatus().isOk
                .expectBody<PlaneResponse>()
                .returnResult()

            val planeFetched = response.responseBody
                ?: throw AssertionError("Plane fetched cannot be null.")

            planeFetched shouldBeEqualTo planeAdded
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2])
    fun `when fetching existing planes should return paginated planes`(pageNumber: Int) {
        runBlocking {
            // given:
            val totalItems = 10L
            val pageSize = 5

            val planeModel = PlaneModelTestsFactory.createAddRequest()
            PlaneModelTestsClient(client).addPlaneModelOk(planeModel)

            val plane = PlaneTestsFactory.createAddRequest(planeModel.name)

            val listPlanesAdded = mutableListOf<PlaneResponse>()

            for (i in 0..<totalItems) {
                val planeAdded = PlaneTestsClient(client)
                    .addPlaneOk(plane)
                    .responseBody
                    ?: throw AssertionError("Plane response cannot be null.")
                listPlanesAdded.add(planeAdded)
            }

            val paginatedPlanesAdded = listPlanesAdded.drop((pageNumber - 1) * pageSize).take(pageSize)

            val expectedPlanesFetched = PaginatedResponse.create(
                pageNumber,
                pageSize,
                totalItems,
                paginatedPlanesAdded
            )

            // when:
            val response = client.get()
                .uri{ builder ->
                    builder.path(FETCH_PLANES_URI)
                        .queryParam("pageNumber", pageNumber)
                        .queryParam("pageSize", pageSize)
                        .build()
                }
                .exchange()
                .expectStatus().isOk
                .expectBody<PaginatedResponse<PlaneResponse>>()
                .returnResult()

            // then:
            val planesFetched = response.responseBody
                ?: throw AssertionError("Plane fetched cannot be null.")

            planesFetched.shouldBeEquivalentTo(expectedPlanesFetched)
        }
    }
}

class PlaneTestsClient(private val client: WebTestClient) {
    companion object {
        const val ADD_PLANE_URI = "v1/planes"
    }

    fun addPlaneOk(requestBody: PlaneRequest) : EntityExchangeResult<PlaneResponse> {
        return client.post()
            .uri(ADD_PLANE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<PlaneResponse>()
            .returnResult()
    }

    fun addPlaneBadRequest(requestBody: PlaneRequest) : EntityExchangeResult<ValidationProblem> {
        return client.post()
            .uri(ADD_PLANE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<ValidationProblem>()
            .returnResult()
    }

    fun addPlaneNotFound(requestBody: PlaneRequest) : EntityExchangeResult<ProblemDetail> {
        return client.post()
            .uri(ADD_PLANE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isNotFound
            .expectBody<ProblemDetail>()
            .returnResult()
    }
}