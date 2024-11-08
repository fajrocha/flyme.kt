package com.faroc.flyme.configurations

import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.client.MockServerClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@ExtendWith(SpringExtension::class)
@TestConfiguration
@Testcontainers
class MockServerConfiguration {
    companion object {
        private val mockServerContainer = MockServerContainer(
            DockerImageName.parse("mockserver/mockserver:mockserver-5.15.0")
        ).apply {
            start()
        }

        private val mockServerClient = MockServerClient(
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

    @Bean
    fun mockServerClient() = mockServerClient
}