package com.faroc.flyme.integration.common

import org.mockserver.client.MockServerClient
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.utility.DockerImageName

abstract class TestContainersTest {
    companion object {
        protected val mockServerContainer = MockServerContainer(
            DockerImageName.parse("mockserver/mockserver:mockserver-5.15.0")
        ).apply {
            start()
        }

        @JvmStatic
        protected val mockServerClient = MockServerClient(
            mockServerContainer.host,
            mockServerContainer.serverPort
        )

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("airport-gap.api-url") { "${mockServerContainer.endpoint}/api" }
            registry.add("airport-gap.port") { mockServerContainer.serverPort }
        }
    }
}