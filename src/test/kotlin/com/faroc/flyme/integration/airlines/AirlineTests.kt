package com.faroc.flyme.integration.airlines

import com.faroc.flyme.airlines.api.requests.AddAirlineRequest
import com.faroc.flyme.airlines.api.responses.AirlinesResponse
import com.faroc.flyme.airlines.domain.errors.AirlineNotFound
import com.faroc.flyme.airlines.infrastructure.AirlineRepository
import com.faroc.flyme.configurations.PostgresConfiguration
import com.faroc.flyme.integration.airlines.utils.AirlineTestsFactory
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
import org.springframework.test.web.reactive.server.expectBodyList
import kotlin.test.Test

const val FETCH_AIRLINE_URI = "v1/airlines"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresConfiguration::class)
class AirlineTests(
    @Autowired
    val client: WebTestClient,
    @Autowired
    val repository: AirlineRepository
) {

    @BeforeEach
    fun clearDatabase() {
        runBlocking {
            repository.deleteAll()
        }
    }

    @Test
    fun `when adding airlines to airport should add airlines`() {
        runBlocking {
            // given:
            val addAirlineRequest = AirlineTestsFactory.createAddRequest()

            // when:
            val airlineAdded = AirlineTestsClient(client)
                .addAirlineOk(addAirlineRequest)
                .responseBody
                ?: throw AssertionError("Response body cannot be null when adding airline.")

            // then:
            repository.existsById(airlineAdded.id).shouldBeTrue()
            airlineAdded.name shouldBeEqualTo addAirlineRequest.name
            airlineAdded.country shouldBeEqualTo addAirlineRequest.country
        }
    }

    @Test
    fun `when fetching airlines from airport should return airlines`() {
        runBlocking {
            // given:
            val addAirlineRequest = AirlineTestsFactory.createAddRequest()

            val expectedAirlinesFetched = listOf(addAirlineRequest)

            AirlineTestsClient(client).addAirlineOk(addAirlineRequest)

            // when:
            val fetchRequest = client.get()
                .uri(FETCH_AIRLINE_URI)
                .exchange()
                .expectStatus().isOk
                .expectBodyList<AirlinesResponse>()
                .returnResult()
            val responseBody = fetchRequest.responseBody ?: listOf()

            // then:
            responseBody.size shouldBeEqualTo expectedAirlinesFetched.size
            responseBody.map { rb -> rb.name } shouldContainAll expectedAirlinesFetched.map { rb -> rb.name }
            responseBody.map { rb -> rb.country } shouldContainAll expectedAirlinesFetched.map { rb -> rb.country }
        }
    }

    @Test
    fun `when fetching non existing airline from airport should return not found`() {
        runBlocking {
            // given:
            val airlineId = 1L

            // when:
            val fetchRequest = client.get()
                .uri(fetchAirlineURI(airlineId))
                .exchange()
                .expectStatus().isNotFound
                .expectBody<ProblemDetail>()
                .returnResult()
            val responseBody = fetchRequest.responseBody

            // then:
            responseBody?.detail shouldBeEqualTo AirlineNotFound.DESCRIPTION
        }
    }

    @Test
    fun `when fetching airline from airport should return airline`() {
        runBlocking {
            // given:
            val addAirlineRequest = AirlineTestsFactory.createAddRequest()

            val airlineAdded = AirlineTestsClient(client)
                .addAirlineOk(addAirlineRequest)
                .responseBody
                ?: throw AssertionError("Response body cannot be null when adding airline.")

            val airlineAddedId = airlineAdded.id

            // when:
            val fetchRequest = client.get()
                .uri(fetchAirlineURI(airlineAddedId))
                .exchange()
                .expectStatus().isOk
                .expectBody<AirlinesResponse>()
                .returnResult()
            val responseBody = fetchRequest.responseBody

            // then:
            responseBody?.id shouldBeEqualTo airlineAddedId
        }
    }

    private fun fetchAirlineURI(id: Long) : String {
        return "$FETCH_AIRLINE_URI/$id"
    }
}

class AirlineTestsClient(private val client: WebTestClient) {
    companion object {
        const val ADD_AIRLINE_URI = "v1/airlines"
    }

    fun addAirlineOk(requestBody: AddAirlineRequest) : EntityExchangeResult<AirlinesResponse> {
        return client.post()
            .uri(ADD_AIRLINE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<AirlinesResponse>()
            .returnResult()
    }
}