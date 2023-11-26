package jmantello.secretrecipeapi.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private var testUserEmail = "testuser@example.com"
    private var testUserPassword = "testpassword"
    private var testUserDisplayName = "testdisplayname"

    @Test
    fun testRegisterLoginLogout() {
        // Register
        val registerUrl = "http://localhost:$port/api/auth/register"
        val registerRequestBody = mapOf(
            "email" to testUserEmail,
            "password" to testUserPassword,
            "displayName" to testUserDisplayName
        )
        val registerResponse: ResponseEntity<String> = restTemplate.postForEntity(registerUrl, registerRequestBody, String::class.java)
        assertEquals(HttpStatus.CREATED, registerResponse.statusCode)

        // Login
        val loginUrl = "http://localhost:$port/api/auth/login"
        val loginRequestBody = mapOf(
            "email" to testUserEmail,
            "password" to testUserPassword
        )
        val loginResponse: ResponseEntity<String> = restTemplate.postForEntity(loginUrl, loginRequestBody, String::class.java)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)

        // Logout
        val logoutUrl = "http://localhost:$port/api/auth/logout"
        val logoutResponse: ResponseEntity<String> = restTemplate.postForEntity(logoutUrl, null, String::class.java)
        assertEquals(HttpStatus.OK, logoutResponse.statusCode)
    }
}