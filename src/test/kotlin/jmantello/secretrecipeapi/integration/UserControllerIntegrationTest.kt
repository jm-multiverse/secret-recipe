package jmantello.secretrecipeapi.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jmantello.secretrecipeapi.entity.LoginUserDTO
import jmantello.secretrecipeapi.entity.RegisterUserDTO
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.util.EndpointBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {
    @Autowired
    private lateinit var helper: TestHelper

    @LocalServerPort
    private var port: Int = 0
    private val host: String = "http://localhost"
    private val endpointBuilder: EndpointBuilder by lazy { EndpointBuilder(host, port) }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    val objectMapper = ObjectMapper()

    private var testUserEmail = "testuser@example.com"
    private var testUserPassword = "testpassword"
    private var testUserDisplayName = "test display name"


    @Test
    fun testUpdateUser() {
        val testUser = helper.getTestUser()
        val updateUrl = endpointBuilder.users
        val changedDisplayName = "test changed display name"

        testUser.displayName = changedDisplayName

        // TODO: Fix test
//        val requestEntity = HttpEntity(testUser)
//        val updateResponse: ResponseEntity<String> = restTemplate.exchange(
//            updateUrl,
//            HttpMethod.PUT,
//            requestEntity,
//            String::class.java
//        )
//        assertEquals(HttpStatus.OK, updateResponse.statusCode)
    }

}