package com.faroc.flyme.integration.airports

import com.faroc.flyme.TestcontainersConfiguration
import com.faroc.flyme.airports.api.request.AirportRequest
import com.faroc.flyme.airports.api.response.AirportResponse
import com.faroc.flyme.airports.domain.errors.AirportConflictIataCode
import com.faroc.flyme.airports.domain.errors.AirportNotFound
import com.faroc.flyme.airports.infrastructure.AirportRepository
import com.faroc.flyme.common.api.middleware.ValidationProblem
import com.faroc.flyme.integration.airports.utils.AirportTestsFactory
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeEquivalentTo
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
import org.springframework.test.web.reactive.server.expectBodyList
import kotlin.test.Test

const val POST_REQUEST_URI = "v1/airports"
const val FETCH_AIRPORTS_REQUEST_URI = "v1/airports"

@OptIn(ExperimentalStdlibApi::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
class AirportTests(
        @Autowired
        private val client: WebTestClient,
        @Autowired
        private val airportRepository: AirportRepository,
) {
    @BeforeEach
    fun clearDatabase() {
        runBlocking {
            airportRepository.deleteAll()
        }
    }

    @Test
    fun `when adding airport and IATA code already exists should return conflict`() {
        runBlocking {
            // given:
            val request = AirportTestsFactory.createAddRequest(iataCode = "LAX")

            addAirport(request)

            // when:
            val responseAdd = client.post()
                    .uri(POST_REQUEST_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isEqualTo(409)
                    .expectBody<ProblemDetail>()
                    .returnResult()

            val responseBodyAdd = responseAdd.responseBody
                    ?: throw AssertionError("Response body cannot be null when adding airport.")

            // then:
            responseBodyAdd.detail shouldBeEqualTo AirportConflictIataCode.DESCRIPTION
            responseBodyAdd.title shouldBeEqualTo AirportConflictIataCode.CODE
        }
    }

    @Test
    fun `when adding airport with invalid request should return bad request`() {
        runBlocking {
            // given:
            val addAirportRequest = AirportTestsFactory.createAddRequest(
                "",
                "",
                "",
                ""
            )

            // when:
            val badRequestResponse = client.post()
                .uri(POST_REQUEST_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addAirportRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody<ValidationProblem>()
                .returnResult()

            val validationProblem = badRequestResponse.responseBody
                ?: throw AssertionError("Response body cannot be null when fetching airport.")

            // then:
            validationProblem.detail shouldBeEqualTo ValidationProblem.DETAIL

            validationProblem.errors["iataCode"]?.shouldContainAll(
                arrayOf(
                    "IATA code should not be blank or omitted.",
                    "IATA code must be between 1 and 50 characters.",
                )
            )

            validationProblem.errors["name"]?.shouldContainAll(
                arrayOf(
                    "Name should not be blank or omitted.",
                    "Name must be between 1 and 50 characters.",
                )
            )

            validationProblem.errors["city"]?.shouldContainAll(
                arrayOf(
                    "City should not be blank or omitted.",
                    "City must be between 1 and 50 characters.",
                )
            )

            validationProblem.errors["country"]?.shouldContainAll(
                arrayOf(
                    "Country should not be blank or omitted.",
                    "Country must be between 1 and 50 characters.",
                )
            )
        }
    }

    @Test
    fun `when adding airport and new IATA code should add airport`() {
        runBlocking {
            // given:
            val addAirportRequest = AirportTestsFactory.createAddRequest()

            // when:
            val responseAdd = addAirport(addAirportRequest)

            val airportAdded = responseAdd.responseBody
                    ?: throw AssertionError("Response body cannot be null when fetching airport.")

            // then:
            addAirportRequest.shouldBeEquivalentTo(airportAdded)
            airportRepository.existsById(airportAdded.id)
        }
    }

    @Test
    fun `when fetching airport and airport does not exist should return not found`() {
        runBlocking {
            // given
            val airportId = 1L

            // when:
            val responseFetch = client.get()
                .uri(fetchAirportURI(airportId))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody<ProblemDetail>()
                .returnResult()

            val responseBodyFetch = responseFetch.responseBody
                ?: throw AssertionError("Response body cannot be null when fetching airport.")

            // then:
            responseBodyFetch.detail shouldBeEqualTo AirportNotFound.DESCRIPTION
            responseBodyFetch.title shouldBeEqualTo AirportNotFound.CODE
        }
    }

    @Test
    fun `when fetching airport and airport exists should return airports`() {
        runBlocking {
            // given:
            val addAirportRequest = AirportTestsFactory.createAddRequest()
            val airportAdded = addAirport(addAirportRequest).responseBody
                ?: throw AssertionError("Response body cannot be null when adding airport.")

            // when:
            val responseFetch = client.get()
                .uri(fetchAirportURI(airportAdded.id))
                .exchange()
                .expectStatus().isOk()
                .expectBody<AirportResponse>()
                .returnResult()

            val responseBodyFetch = responseFetch.responseBody
                ?: throw AssertionError("Response body cannot be null when fetching airport.")

            // then:
            airportAdded.shouldBeEquivalentTo(responseBodyFetch)
        }
    }

    @Test
    fun `when fetching airports should return airports`() {
        runBlocking {
            // given:
            val addAirportRequest = AirportTestsFactory.createAddRequest()
            val airportAdded = addAirport(addAirportRequest).responseBody
                ?: throw AssertionError("Response body cannot be null when adding airport.")
            val expectedAirportsAdded = listOf(airportAdded)

            // when:
            val responseFetch = client.get()
                .uri(FETCH_AIRPORTS_REQUEST_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList<AirportResponse>()
                .returnResult()

            val airportsFetched = responseFetch.responseBody
                ?: throw AssertionError("Response body cannot be null when fetching airports.")

            // then:
            airportsFetched.shouldBeEquivalentTo(expectedAirportsAdded)
        }
    }

    private fun addAirport(requestBody: AirportRequest) : EntityExchangeResult<AirportResponse> {
        return client.post()
                .uri(POST_REQUEST_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated
                .expectBody<AirportResponse>()
                .returnResult()
    }

    private fun fetchAirportURI(id: Long) : String {
        return "$FETCH_AIRPORTS_REQUEST_URI/$id"
    }
}