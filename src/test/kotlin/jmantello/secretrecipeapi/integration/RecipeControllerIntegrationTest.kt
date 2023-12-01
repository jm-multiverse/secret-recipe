package jmantello.secretrecipeapi.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jmantello.secretrecipeapi.entity.*
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
class RecipeControllerIntegrationTest {

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
    fun testPublishRecipe() {
        val publishUrl = endpointBuilder.recipes

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

        // Login
        val loginUrl = endpointBuilder.login
        val loginRequestBody = LoginUserDTO(
            testUserEmail,
            testUserPassword
        )
        val loginResponse: ResponseEntity<String> = restTemplate.postForEntity(loginUrl, loginRequestBody, String::class.java)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)

        // Create Recipe
        val publisherId = testUser.id
        val title = "Cheese Steak Sandwich"
        val content = "I love cheese, I love steak, and I love sandwiches. This means that a cheese steak sandwich is sure to knock it out of the park. First you take the cheese..."
        val createRecipeRequest = CreateRecipeRequest(
            publisherId,
            title,
            content
        )

        val requestEntity = HttpEntity(createRecipeRequest)
        val postResponse: ResponseEntity<String> = restTemplate.exchange(
            publishUrl,
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
        assertEquals(HttpStatus.CREATED, postResponse.statusCode)

        // Deserialize response
        val createdRecipe: Recipe = objectMapper.readValue(postResponse.body!!)
        assertEquals(publisherId, createdRecipe.publisher?.id ?: false)
        assertEquals(title, createdRecipe.title)
        assertEquals(content, createdRecipe.content)

    }

}