package com.faroc.flyme.integration.airlines

import com.faroc.flyme.TestcontainersConfiguration
import com.faroc.flyme.airlines.api.requests.AddAirlineRequest
import com.faroc.flyme.airlines.api.responses.AirlinesResponse
import com.faroc.flyme.airlines.domain.Airline
import com.faroc.flyme.airlines.infrastructure.AirlineRepository
import com.faroc.flyme.airlines.domain.errors.AirlineNotFound
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
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters
import kotlin.test.Test


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
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
            val requestBody = Airline("Bryanair", "Iceland")

            // when:
            val requestResult = client.post()
                .uri("/v1/airlines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .exchange()
                .expectStatus().isCreated
                .expectBody<AirlinesResponse>()
                .returnResult()

            // then:
            val responseBody = requestResult.responseBody

            val id = responseBody?.id ?: -1
            repository.existsById(id).shouldBeTrue()

            responseBody?.name shouldBeEqualTo requestBody.name
            responseBody?.country shouldBeEqualTo requestBody.country
        }
    }

    @Test
    fun `when fetching airlines from airport should return airlines`() {
        runBlocking {
            // given:
            val requestBody = Airline("Bryanair", "Iceland")

            val expectedAirlinesFetched = listOf(requestBody)

            client.post()
                .uri("/v1/airlines") // Ensure to have the leading slash
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .exchange()

            // when:
            val fetchRequest = client.get()
                .uri("/v1/airlines")
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
                .uri("/v1/airlines/$airlineId")
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
            val requestBody = Airline("Bryanair", "Iceland")

            val result = client.post()
                .uri("/v1/airlines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .exchange()
                .expectBody<AirlinesResponse>()
                .returnResult()

            val airlineAdded = result.responseBody
            val airlineAddedId = airlineAdded?.id ?: -1

            // when:
            val fetchRequest = client.get()
                .uri("/v1/airlines/$airlineAddedId")
                .exchange()
                .expectStatus().isOk
                .expectBody<AirlinesResponse>()
                .returnResult()
            val responseBody = fetchRequest.responseBody

            // then:
            responseBody?.id shouldBeEqualTo airlineAddedId
        }
    }
}