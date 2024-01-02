package jmantello.secretrecipeapi.integration

import jmantello.secretrecipeapi.transfer.request.UserLoginRequest
import jmantello.secretrecipeapi.transfer.request.RegisterUserRequest
import jmantello.secretrecipeapi.transfer.model.UserDTO
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.transfer.response.UserLoginResponse
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.Result
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.toEntity
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var reviewService: ReviewService

    // Test User
    private lateinit var testUser: UserDTO
    private var testUserEmail = "authtestuser@example.com"
    private var testUserPassword = "authtestpassword"
    private var testUserDisplayName = "authtestdisplayname"

    @Test
    @Order(0)
    fun testUserRegistration(): Unit = runBlocking {

        val registerUrl = endpoints.register
        val registerRequestBody = RegisterUserRequest(
            testUserEmail,
            testUserPassword,
            testUserDisplayName,
        )

        val response = webClient.post()
            .uri(registerUrl)
            .bodyValue(registerRequestBody)
            .exchangeToMono { it.toEntity<ApiResponse<UserDTO>>() }
            .awaitSingle()

        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)

        val apiResponse = response.body ?: fail("Response body was null.")
        val userDTO = apiResponse.data ?: fail("Response body data was null.")

        Assertions.assertNotNull(userDTO.id)
        Assertions.assertEquals(testUserEmail, userDTO.email)
        Assertions.assertEquals(testUserDisplayName, userDTO.displayName)

        // Set test user
        val result = userService.findById(userDTO.id)
        testUser = when (result) {
            is Result.Success -> result.data
            is Result.Error -> fail(result.message)
        }
    }

    @Test
    @Order(1)
    fun testUserLogin(): Unit = runBlocking {

        val loginUrl = endpoints.login
        val loginRequestBody = UserLoginRequest(
            testUserEmail,
            testUserPassword
        )

        val response = webClient.post()
            .uri(loginUrl)
            .bodyValue(loginRequestBody)
            .exchangeToMono { it.toEntity<ApiResponse<UserLoginResponse>>() }
            .awaitSingle()

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        val apiResponse = response.body ?: fail("Response body was null.")
        val userLoginResponse = apiResponse.data ?: fail("Response body data was null.")

        Assertions.assertNotNull(userLoginResponse.accessToken)
        Assertions.assertNotNull(userLoginResponse.refreshToken)
        Assertions.assertNotNull(userLoginResponse.user)

        assertNull(apiResponse.error)
    }

    @Test
    @Order(2)
    fun testLogout() = runBlocking {
        // Logout
        val logoutUrl = endpoints.logout
        val logoutResponse = webClient.post()
            .uri(logoutUrl)
            .exchangeToMono { it.toEntity<ApiResponse<String>>() }
            .awaitSingle()

        Assertions.assertEquals(HttpStatus.OK, logoutResponse.statusCode)
    }
}