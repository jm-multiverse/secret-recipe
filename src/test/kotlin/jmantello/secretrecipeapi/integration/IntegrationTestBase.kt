package jmantello.secretrecipeapi.integration

import jmantello.secretrecipeapi.util.Endpoints
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.reactive.function.client.WebClient

abstract  class IntegrationTestBase {

    @LocalServerPort
    private var port: Int = 0
    private var host: String = "http://localhost"
    val endpoints: Endpoints by lazy { Endpoints(host, port) }

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder
    lateinit var webClient: WebClient

    @BeforeAll
    fun setupBase() {
        val baseUrl = "$host:$port"
        webClient = webClientBuilder.baseUrl(baseUrl).build()
    }
}