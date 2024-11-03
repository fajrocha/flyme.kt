package com.faroc.flyme.integration.airports.utils

import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

class AirportDataServiceMock(
    private val mockServerClient: MockServerClient
) {
    fun setupAirportDataFetchOk(iataCode: String) {
        val airportDataResponse = dataFetchOkResponse(iataCode)

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

    private fun dataFetchOkResponse(iataCode: String) : String {
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
}