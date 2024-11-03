package com.faroc.flyme.integration.airports.utils

import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.Parameter
import org.mockserver.model.Parameters

class AirportDataServiceMock(
    private val mockServerClient: MockServerClient
) {
    fun setupAirportDataFetchOk(iataCode: String) {
        val airportDataResponse = airportDataFetchOkResponse(iataCode)

        mockServerClient.`when`(
            request()
                .withMethod("GET")
                .withPath("/api/airports/${iataCode}")
        )
            .respond(
                response()
                    .withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(airportDataResponse.trimIndent())
            )

    }

    fun setupAirportDistanceDataFetchOk(departureIataCode: String, arrivalIataCode: String) {
        val airportDistancesDataResponse = airportDistanceDataFetchOkResponse(departureIataCode, arrivalIataCode)

        mockServerClient.`when`(
            request()
                .withMethod("POST")
                .withPath("/api/airports/distance")
                .withQueryStringParameters(
                    Parameters(
                        Parameter("from", departureIataCode),
                        Parameter("to", arrivalIataCode),
                    )
                )
        )
            .respond(
                response()
                    .withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(airportDistancesDataResponse.trimIndent())
            )
    }

    fun setupAirportDistanceDataFetch4xx(
        departureIataCode: String,
        arrivalIataCode: String,
        statusCode: Int = 400) {
        mockServerClient.`when`(
            request()
                .withMethod("POST")
                .withPath("/api/airports/distance")
                .withQueryStringParameters(
                    Parameters(
                        Parameter("from", departureIataCode),
                        Parameter("to", arrivalIataCode),
                    )
                )
        )
            .respond(
                response()
                    .withStatusCode(statusCode)
                    .withHeader("Content-Type", "application/json")
                    .withBody("")
            )
    }

    fun setupAirportDistanceDataFetch500(departureIataCode: String, arrivalIataCode: String) {
        mockServerClient.`when`(
            request()
                .withMethod("POST")
                .withPath("/api/airports/distance")
                .withQueryStringParameters(
                    Parameters(
                        Parameter("from", departureIataCode),
                        Parameter("to", arrivalIataCode),
                    )
                )
        )
            .respond(
                response()
                    .withStatusCode(500)
                    .withHeader("Content-Type", "application/json")
                    .withBody("")
            )
    }

    private fun airportDataFetchOkResponse(iataCode: String) : String {
        return """
            {
                "data": {
                    "id": "$iataCode",
                    "type": "airport",
                    "attributes": {
                        "name": "Los Angeles International Airport",
                        "city": "Los Angeles",
                        "country": "United States",
                        "iata": "$iataCode",
                        "icao": "KLAX",
                        "latitude": "33.942501",
                        "longitude": "-118.407997",
                        "altitude": 125,
                        "timezone": "America/Los_Angeles"
                    }
                }
            }
            """
    }

    private fun airportDistanceDataFetchOkResponse(departureIataCode: String, arrivalIataCode: String) : String {
        return """
            {
                "data": {
                    "id": "$departureIataCode-$arrivalIataCode",
                    "type": "airport_distance",
                    "attributes": {
                        "from_airport": {
                            "id": 2687,
                            "name": "Los Angeles International Airport",
                            "city": "Los Angeles",
                            "country": "United States",
                            "iata": "$departureIataCode",
                            "icao": "KLAX",
                            "latitude": "33.942501",
                            "longitude": "-118.407997",
                            "altitude": 125,
                            "timezone": "America/Los_Angeles"
                        },
                        "to_airport": {
                            "id": 953,
                            "name": "Barcelona International Airport",
                            "city": "Barcelona",
                            "country": "Spain",
                            "iata": "$arrivalIataCode",
                            "icao": "LEBL",
                            "latitude": "41.2971",
                            "longitude": "2.07846",
                            "altitude": 12,
                            "timezone": "Europe/Madrid"
                        },
                        "kilometers": 9674.260707908115,
                        "miles": 6007.122172419479,
                        "nautical_miles": 5220.043946209944
                    }
                }
            }
            """
    }
}