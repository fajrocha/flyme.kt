package com.faroc.flyme.integration.airline

import com.faroc.flyme.TestcontainersConfiguration
import com.faroc.flyme.airline.api.requests.AddAirlineRequest
import com.faroc.flyme.airline.api.responses.AirlinesResponse
import com.faroc.flyme.airline.infrastructure.AirlineRepository
import com.faroc.flyme.airline.domain.errors.AirlineNotFound
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
    @Autowired val client: WebTestClient,
    @Autowired val repository: AirlineRepository
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
            val requestBody = getTwoRecordsRequestBody()
            val requestNames = getTwoRecordsRequestBody().map { rb -> rb.name }
            val requestCountries = getTwoRecordsRequestBody().map { rb -> rb.country }

            // when:
            val requestResult = client.post()
                .uri("/v1/airlines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getTwoRecordsRequestBody()))
                .exchange()
                .expectStatus().isCreated
                .expectBodyList<AirlinesResponse>()
                .returnResult()

            // then:
            val responseBody = requestResult.responseBody ?: listOf()

            responseBody.size shouldBeEqualTo requestBody.size
            repository.count() shouldBeEqualTo requestBody.size.toLong()

            responseBody.map { rb -> rb.id }.forEach {
                i -> repository.existsById(i).shouldBeTrue()
            }
            val ids = responseBody.map { rb -> rb.id }
            repository.deleteAllById(ids)

            val responseNames = responseBody.map { rb -> rb.name }
            responseNames shouldContainAll requestNames

            val responseCountries = responseBody.map { rb -> rb.country }
            responseCountries shouldContainAll requestCountries
        }
    }

    @Test
    fun `when fetching airlines from airport should return airlines`() {
        runBlocking {
            // given:
            client.post()
                .uri("/v1/airlines") // Ensure to have the leading slash
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getTwoRecordsRequestBody()))
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
            responseBody.size shouldBeEqualTo getTwoRecordsRequestBody().size
            responseBody.map { rb -> rb.name } shouldContainAll getTwoRecordsRequestBody().map { rb -> rb.name }
            responseBody.map { rb -> rb.country } shouldContainAll getTwoRecordsRequestBody().map { rb -> rb.country }

            // teardown
            val ids = responseBody.map { rb -> rb.id }
            repository.deleteAllById(ids)
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
            val result = client.post()
                .uri("/v1/airlines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getOneRecordRequestBody()))
                .exchange()
                .expectBodyList<AirlinesResponse>()
                .returnResult()

            val airlineAdded = result.responseBody ?: listOf()
            val airlineAddedId = airlineAdded.first().id

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

    private fun getTwoRecordsRequestBody() : List<AddAirlineRequest> {
        return listOf(
            AddAirlineRequest("Bryanair", "Iceland"),
            AddAirlineRequest("Hardyjet", "England"),
        )
    }

    private fun getOneRecordRequestBody() : List<AddAirlineRequest> {
        return listOf(
            AddAirlineRequest("Bryanair", "Iceland"),
        )
    }
}