package com.faroc.flyme.integration.planes

import com.faroc.flyme.TestcontainersConfiguration
import com.faroc.flyme.integration.planes.utils.PlaneModelRequestFactory
import com.faroc.flyme.integration.planes.utils.PlaneRequestFactory
import com.faroc.flyme.planes.api.requests.PlaneModelRequest
import com.faroc.flyme.planes.api.requests.PlaneRequest
import com.faroc.flyme.planes.api.responses.PlaneModelResponse
import com.faroc.flyme.planes.api.responses.PlaneResponse
import com.faroc.flyme.planes.infrastructure.PlaneRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
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
    private val repository: PlaneRepository
) {
    @Test
    fun `when adding plane should add plane to platform`() {
        val planeModel = PlaneModelRequestFactory.create()
        addPlaneModel(planeModel)

        val plane = PlaneRequestFactory.create(planeModel.name)

        val planeAddedResponse = addPlane(plane)
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