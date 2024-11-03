package com.faroc.flyme.integration.flights

import com.faroc.flyme.airlines.domain.errors.AirlineNotFound
import com.faroc.flyme.common.infrastructure.airportgap.AirportGapService
import com.faroc.flyme.configurations.PostgresConfiguration
import com.faroc.flyme.flights.api.requests.ScheduleFlightRequest
import com.faroc.flyme.flights.api.responses.ScheduleFlightResponse
import com.faroc.flyme.flights.domain.errors.FlightArrivalAirportNotFound
import com.faroc.flyme.flights.domain.errors.FlightDepartAirportNotFound
import com.faroc.flyme.flights.infrastructure.repositories.FlightRepository
import com.faroc.flyme.integration.airlines.AirlineTestsClient
import com.faroc.flyme.integration.airlines.utils.AirlineTestsFactory
import com.faroc.flyme.integration.airports.AirportTestsClient
import com.faroc.flyme.integration.airports.utils.AirportDataServiceMock
import com.faroc.flyme.integration.airports.utils.AirportTestsFactory
import com.faroc.flyme.integration.common.TestContainersTest
import com.faroc.flyme.integration.flights.utils.FlightTestsFactory
import com.faroc.flyme.integration.planes.PlaneModelTestsClient
import com.faroc.flyme.integration.planes.PlaneTestsClient
import com.faroc.flyme.integration.planes.utils.PlaneModelTestsFactory
import com.faroc.flyme.integration.planes.utils.PlaneTestsFactory
import com.faroc.flyme.planes.domain.errors.PlaneNotFound
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.flywaydb.core.Flyway
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
import java.time.LocalDateTime
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresConfiguration::class)
class FlightsTests(
    @Autowired
    private val flightRepository: FlightRepository,
    @Autowired
    private val client: WebTestClient,
    ) : TestContainersTest() {

    @BeforeEach
    fun setup(@Autowired flyway: Flyway) {
        flyway.clean()
        flyway.migrate()
        mockServerClient.reset()
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
                .scheduleFlightProblemNotFound(scheduleFlightRequest)
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
                .scheduleFlightProblemNotFound(scheduleFlightRequest)
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
                arrivalIata,
                1L,
                "United Airlines",
                LocalDateTime.now()
            )

            // when:
            val response = FlightTestsClient(client)
                .scheduleFlightProblemNotFound(scheduleFlightRequest)
                .responseBody ?: throw AssertionError("Response body cannot be null when scheduling flight")

            // then:
            response.detail shouldBeEqualTo PlaneNotFound.DESCRIPTION
            response.title shouldBeEqualTo PlaneNotFound.CODE
        }
    }

    @Test
    fun `when scheduling and airline does not exist should return not found`() {
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

            val planeModel = PlaneModelTestsFactory.createAddRequest()
            PlaneModelTestsClient(client).addPlaneModelOk(planeModel)

            val plane = PlaneTestsFactory.createAddRequest(planeModel.name)
            val existingPlane = PlaneTestsClient(client).addPlaneOk(plane).responseBody
                ?: throw AssertionError("Response body cannot be null when adding plane.")

            val scheduleFlightRequest = FlightTestsFactory.createScheduleFlightRequest(
                departureIata,
                arrivalIata,
                existingPlane.id,
                "United Airlines",
                LocalDateTime.now()
            )

            // when:
            val response = FlightTestsClient(client)
                .scheduleFlightProblemNotFound(scheduleFlightRequest)
                .responseBody ?: throw AssertionError("Response body cannot be null when scheduling flight")

            // then:
            response.detail shouldBeEqualTo AirlineNotFound.DESCRIPTION
            response.title shouldBeEqualTo AirlineNotFound.CODE
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [400, 401, 404, 422, 429])
    fun `when scheduling and airport data API fails with 4xx error`(statusCode : Int) {
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

            val planeModel = PlaneModelTestsFactory.createAddRequest()
            PlaneModelTestsClient(client).addPlaneModelOk(planeModel)

            val plane = PlaneTestsFactory.createAddRequest(planeModel.name)
            val existingPlane = PlaneTestsClient(client).addPlaneOk(plane).responseBody
                ?: throw AssertionError("Response body cannot be null when adding plane.")

            val addAirline = AirlineTestsFactory.createAddRequest()
            val existingAirline = AirlineTestsClient(client)
                .addAirlineOk(addAirline)
                .responseBody
                ?: throw AssertionError("Response body cannot be null when adding plane.")

            AirportDataServiceMock(mockServerClient).setupAirportDistanceDataFetch4xx(departureIata, arrivalIata, statusCode)

            val scheduleFlightRequest = FlightTestsFactory.createScheduleFlightRequest(
                departureIata,
                arrivalIata,
                existingPlane.id,
                existingAirline.name,
                LocalDateTime.now()
            )

            // when:
            val scheduledFlight = FlightTestsClient(client)
                .scheduleFlightProblem5xx(scheduleFlightRequest)
                .responseBody ?: throw AssertionError("Response body cannot be null when scheduling flight")

            // then:
            scheduledFlight.detail shouldBeEqualTo AirportGapService.AIRPORTS_DISTANCE_ERROR_DESCRIPTION
        }
    }

    @Test
    fun `when scheduling and airport data API fails with 5xx error`() {
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

            val planeModel = PlaneModelTestsFactory.createAddRequest()
            PlaneModelTestsClient(client).addPlaneModelOk(planeModel)

            val plane = PlaneTestsFactory.createAddRequest(planeModel.name)
            val existingPlane = PlaneTestsClient(client).addPlaneOk(plane).responseBody
                ?: throw AssertionError("Response body cannot be null when adding plane.")

            val addAirline = AirlineTestsFactory.createAddRequest()
            val existingAirline = AirlineTestsClient(client)
                .addAirlineOk(addAirline)
                .responseBody
                ?: throw AssertionError("Response body cannot be null when adding plane.")

            AirportDataServiceMock(mockServerClient).setupAirportDistanceDataFetch500(departureIata, arrivalIata)

            val scheduleFlightRequest = FlightTestsFactory.createScheduleFlightRequest(
                departureIata,
                arrivalIata,
                existingPlane.id,
                existingAirline.name,
                LocalDateTime.now()
            )

            // when:
            val scheduledFlight = FlightTestsClient(client)
                .scheduleFlightProblem5xx(scheduleFlightRequest)
                .responseBody ?: throw AssertionError("Response body cannot be null when scheduling flight")

            // then:
            scheduledFlight.detail shouldBeEqualTo AirportGapService.AIRPORTS_DISTANCE_ERROR_DESCRIPTION
        }
    }

    @Test
    fun `when scheduling and all conditions met should schedule flight`() {
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

            val planeModel = PlaneModelTestsFactory.createAddRequest()
            PlaneModelTestsClient(client).addPlaneModelOk(planeModel)

            val plane = PlaneTestsFactory.createAddRequest(planeModel.name)
            val existingPlane = PlaneTestsClient(client).addPlaneOk(plane).responseBody
                ?: throw AssertionError("Response body cannot be null when adding plane.")

            val addAirline = AirlineTestsFactory.createAddRequest()
            val existingAirline = AirlineTestsClient(client)
                .addAirlineOk(addAirline)
                .responseBody
                ?: throw AssertionError("Response body cannot be null when adding plane.")

            AirportDataServiceMock(mockServerClient).setupAirportDistanceDataFetchOk(departureIata, arrivalIata)

            val scheduleFlightRequest = FlightTestsFactory.createScheduleFlightRequest(
                departureIata,
                arrivalIata,
                existingPlane.id,
                existingAirline.name,
                LocalDateTime.now()
            )

            // when:
            val scheduledFlight = FlightTestsClient(client)
                .scheduleFlightOk(scheduleFlightRequest)
                .responseBody ?: throw AssertionError("Response body cannot be null when scheduling flight")

            // then:
            flightRepository.existsById(scheduledFlight.id).shouldBeTrue()
        }
    }
}

class FlightTestsClient(private val client: WebTestClient) {
    companion object {
        const val SCHEDULE_FLIGHT_URI = "v1/flights"
    }

    fun scheduleFlightOk(requestBody: ScheduleFlightRequest) : EntityExchangeResult<ScheduleFlightResponse> {
        return client.post()
            .uri(SCHEDULE_FLIGHT_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
//            .expectStatus().isCreated
            .expectBody<ScheduleFlightResponse>()
            .returnResult()
    }

    fun scheduleFlightProblemNotFound(requestBody: ScheduleFlightRequest) : EntityExchangeResult<ProblemDetail> {
        return client.post()
            .uri(SCHEDULE_FLIGHT_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isNotFound
            .expectBody<ProblemDetail>()
            .returnResult()
    }

    fun scheduleFlightProblem5xx(requestBody: ScheduleFlightRequest) : EntityExchangeResult<ProblemDetail> {
        return client.post()
            .uri(SCHEDULE_FLIGHT_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody<ProblemDetail>()
            .returnResult()
    }
}