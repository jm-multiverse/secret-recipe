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

    @LocalServerPort
    private var port: Int = 0
    private var host: String = "http://localhost"
    private val endpointBuilder: EndpointBuilder by lazy { EndpointBuilder(host, port) }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    val objectMapper = ObjectMapper()

    private var testUserEmail = "testuser@example.com"
    private var testUserPassword = "testpassword"
    private var testUserDisplayName = "test display name"


    @Test
    fun testUpdateUser() {
        // Register
        val registerUrl = endpointBuilder.register
        val registerRequestBody = RegisterUserDTO(
            testUserEmail,
            testUserPassword,
            testUserDisplayName
        )
        val registerResponse: ResponseEntity<String> = restTemplate.postForEntity(registerUrl, registerRequestBody, String::class.java)
        assertEquals(HttpStatus.CREATED, registerResponse.statusCode)

        // Deserialize User
        val testUser: User = objectMapper.readValue(registerResponse.body!!)
        assertEquals(testUserEmail, testUser.email)
        assertEquals(testUserDisplayName, testUser.displayName)
        // TODO: assertTrue { testUser.validatePassword(testUserPassword) }

        // Login
        val loginUrl = endpointBuilder.login
        val loginRequestBody = LoginUserDTO(
            testUserEmail,
            testUserPassword
        )
        val loginResponse: ResponseEntity<String> = restTemplate.postForEntity(loginUrl, loginRequestBody, String::class.java)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)

        // Update User
        val updateUrl = endpointBuilder.users
        val changedDisplayName = "test changed display name"
        testUser.displayName = changedDisplayName
        val requestEntity = HttpEntity(testUser)
        val updateResponse: ResponseEntity<String> = restTemplate.exchange(
            updateUrl,
            HttpMethod.PUT,
            requestEntity,
            String::class.java
        )
        // TODO: assertEquals(HttpStatus.OK, updateResponse.statusCode)
    }
}