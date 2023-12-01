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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {


    @LocalServerPort
    private var port: Int = 0
    private var host: String = "http://localhost"
    private val endpointBuilder: EndpointBuilder by lazy { EndpointBuilder(host, port) }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    val objectMapper = ObjectMapper()

    private var testUserEmail = "testuser@example.com"
    private var testUserPassword = "testpassword"
    private var testUserDisplayName = "testdisplayname"

    @Test
    fun testRegisterLoginLogout() {
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

        // Logout
        val logoutUrl = endpointBuilder.logout
        val logoutResponse: ResponseEntity<String> = restTemplate.postForEntity(logoutUrl, null, String::class.java)
        assertEquals(HttpStatus.OK, logoutResponse.statusCode)
    }
}