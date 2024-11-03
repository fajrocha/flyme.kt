package com.faroc.flyme.integration.flights

import com.faroc.flyme.airlines.infrastructure.AirlineRepository
import com.faroc.flyme.airports.infrastructure.AirportRepository
import com.faroc.flyme.configurations.MockServerConfiguration
import com.faroc.flyme.configurations.PostgresConfiguration
import com.faroc.flyme.flights.api.requests.ScheduleFlightRequest
import com.faroc.flyme.flights.api.responses.ScheduleFlightResponse
import com.faroc.flyme.flights.domain.errors.FlightArrivalAirportNotFound
import com.faroc.flyme.flights.domain.errors.FlightDepartAirportNotFound
import com.faroc.flyme.integration.airports.AirportTestsClient
import com.faroc.flyme.integration.airports.utils.AirportDataServiceMock
import com.faroc.flyme.integration.airports.utils.AirportTestsFactory
import com.faroc.flyme.integration.flights.utils.FlightTestsFactory
import com.faroc.flyme.planes.domain.errors.PlaneNotFound
import com.faroc.flyme.planes.infrastructure.PlaneModelRepository
import com.faroc.flyme.planes.infrastructure.PlaneRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.mockserver.client.MockServerClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.LocalDateTime
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresConfiguration::class, MockServerConfiguration::class)
class FlightsTests(
    @Autowired
    private val planeModelRepository: PlaneModelRepository,
    @Autowired
    private val planeRepository: PlaneRepository,
    @Autowired
    private val airlineRepository: AirlineRepository,
    @Autowired
    private val airportRepository: AirportRepository,
    @Autowired
    private val client: WebTestClient,
    @Autowired
    private val mockServerClient: MockServerClient,
    ) {

    @BeforeEach
    fun setup() {
        runBlocking {
            airportRepository.deleteAll()
            airlineRepository.deleteAll()
            planeRepository.deleteAll()
            planeModelRepository.deleteAll()
            mockServerClient.reset()
        }
    }

    @Test
    fun `when scheduling flight and departure airport does not exist should return not found`() {
        runBlocking {
            // given:
            val scheduleFlightRequest = FlightTestsFactory.createScheduleFlightRequest(
                "LAX",
                "BCN",
                1L,
                "United Airlines",
                LocalDateTime.now()
            )

            // when:
            val response = FlightTestsClient(client)
                .scheduleFlightProblem404(scheduleFlightRequest)
                .responseBody ?: throw AssertionError("Response body cannot be null when scheduling flight")

            // then:
            response.detail shouldBeEqualTo FlightDepartAirportNotFound.DESCRIPTION
            response.title shouldBeEqualTo FlightDepartAirportNotFound.CODE
        }
    }

    @Test
    fun `when scheduling flight and arrival airport does not exist should return not found`() {
        runBlocking {
            // given:
            val departureIata = "LAX"

            val departureAirport = AirportTestsFactory.createAddRequest(
                departureIata
            )

            AirportDataServiceMock(mockServerClient).setupAirportDataFetchOk(departureIata)
            AirportTestsClient(client).addAirportOk(departureAirport)

            val scheduleFlightRequest = FlightTestsFactory.createScheduleFlightRequest(
                departureIata,
                "BCN",
                1L,
                "United Airlines",
                LocalDateTime.now()
            )

            // when:
            val response = FlightTestsClient(client)
                .scheduleFlightProblem404(scheduleFlightRequest)
                .responseBody ?: throw AssertionError("Response body cannot be null when scheduling flight")

            // then:
            response.detail shouldBeEqualTo FlightArrivalAirportNotFound.DESCRIPTION
            response.title shouldBeEqualTo FlightArrivalAirportNotFound.CODE
        }
    }

    @Test
    fun `when scheduling flight plane does not exist should return not found`() {
        runBlocking {
            // given:
            val departureIata = "LAX"
            val departureAirport = AirportTestsFactory.createAddRequest(
                departureIata
            )

            AirportDataServiceMock(mockServerClient).setupAirportDataFetchOk(departureIata)
            AirportTestsClient(client).addAirportOk(departureAirport)

            val arrivalIata = "BCN"
            val arrivalAirport = AirportTestsFactory.createAddRequest(
                arrivalIata,
                "Barcelona Airport",
                "Barcelona",
                "Spain"
            )

            AirportDataServiceMock(mockServerClient).setupAirportDataFetchOk(arrivalIata)
            AirportTestsClient(client).addAirportOk(arrivalAirport)

            val scheduleFlightRequest = FlightTestsFactory.createScheduleFlightRequest(
                departureIata,
                "BCN",
                1L,
                "United Airlines",
                LocalDateTime.now()
            )

            // when:
            val response = FlightTestsClient(client)
                .scheduleFlightProblem404(scheduleFlightRequest)
                .responseBody ?: throw AssertionError("Response body cannot be null when scheduling flight")

            // then:
            response.detail shouldBeEqualTo PlaneNotFound.DESCRIPTION
            response.title shouldBeEqualTo PlaneNotFound.CODE
        }
    }
}

class FlightTestsClient(private val client: WebTestClient) {
    companion object {
        const val SCHEDULE_FLIGHT_URI = "v1/flights"
    }

    fun scheduleFlight(requestBody: ScheduleFlightRequest) : EntityExchangeResult<ScheduleFlightResponse> {
        return client.post()
            .uri(SCHEDULE_FLIGHT_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<ScheduleFlightResponse>()
            .returnResult()
    }

    fun scheduleFlightProblem404(requestBody: ScheduleFlightRequest) : EntityExchangeResult<ProblemDetail> {
        return client.post()
            .uri(SCHEDULE_FLIGHT_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isNotFound
            .expectBody<ProblemDetail>()
            .returnResult()
    }
}