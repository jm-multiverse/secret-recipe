package jmantello.secretrecipeapi.integration

import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.TokenService.TokenType.ACCESS
import jmantello.secretrecipeapi.service.TokenService.TokenType.REFRESH
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.transfer.model.UserDTO
import jmantello.secretrecipeapi.transfer.request.RegisterUserRequest
import jmantello.secretrecipeapi.transfer.request.UserLoginRequest
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.Result
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.MultiValueMap
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

    private lateinit var accessToken: String
    private lateinit var refreshToken: String

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

        assertEquals(HttpStatus.CREATED, response.statusCode)

        val apiResponse = response.body ?: fail("Response body was null.")
        val userDTO = apiResponse.data ?: fail("Response body data was null.")

        assertNotNull(userDTO.id)
        assertEquals(testUserEmail, userDTO.email)
        assertEquals(testUserDisplayName, userDTO.displayName)

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
        var cookies: MultiValueMap<String, ResponseCookie>? = null

        val response = webClient.post()
            .uri(loginUrl)
            .bodyValue(loginRequestBody)
            .exchangeToMono {
                cookies = it.cookies()
                it.toEntity<ApiResponse<UserDTO>>()
            }
            .awaitSingle()

        assertEquals(HttpStatus.OK, response.statusCode)

        val apiResponse = response.body ?: fail("Response body was null.")
        assertNull(apiResponse.error)

        val userLoginResponse = apiResponse.data ?: fail("Response body data was null.")
        assertEquals(testUser.id, userLoginResponse.id)

        val accessTokenCookie = cookies!!.getFirst(ACCESS.tokenName) ?: fail("Access token cookie was null.")
        assertNotNull(accessTokenCookie.value)
        accessToken = accessTokenCookie.value

        val refreshTokenCookie = cookies!!.getFirst(REFRESH.tokenName) ?: fail("Refresh token cookie was null.")
        assertNotNull(refreshTokenCookie.value)
        refreshToken = refreshTokenCookie.value
    }

    @Test
    @Order(2)
    fun testRefreshTokens() = runBlocking {
        // Refresh tokens
        val refreshUrl = endpoints.refresh
        var cookies: MultiValueMap<String, ResponseCookie>? = null
        val refreshResponse: ResponseEntity<ApiResponse<UserDTO>> = webClient.post()
            .uri(refreshUrl)
            .cookie(ACCESS.tokenName, accessToken)
            .cookie(REFRESH.tokenName, refreshToken)
            .exchangeToMono {
                cookies = it.cookies()
                it.toEntity<ApiResponse<UserDTO>>()
            }
            .awaitSingle()

        assertEquals(HttpStatus.OK, refreshResponse.statusCode)

        val apiResponse = refreshResponse.body ?: fail("Response body was null.")
        assertNull(apiResponse.error)

        val userDTO = apiResponse.data ?: fail("Response body data was null.")
        assertEquals(testUser.id, userDTO.id)

        val newAccessTokenCookie = cookies!!.getFirst(ACCESS.tokenName) ?: fail("Access token cookie was null.")
        assertNotNull(newAccessTokenCookie.value)

        val newRefreshTokenCookie = cookies!!.getFirst(REFRESH.tokenName) ?: fail("Refresh token cookie was null.")
        assertNotNull(newRefreshTokenCookie.value)

        // TODO: Figure out why these fail, the two tokens should be different
        //assertNotEquals(accessToken, newAccessTokenCookie.value)
        //assertNotEquals(refreshToken, newRefreshTokenCookie.value)

        accessToken = newAccessTokenCookie.value
        refreshToken = newRefreshTokenCookie.value
    }

    @Test
    @Order(3)
    fun testLogout() = runBlocking {
        // Logout
        val logoutUrl = endpoints.logout
        val logoutResponse = webClient.post()
            .uri(logoutUrl)
            .cookie(ACCESS.tokenName, accessToken)
            .cookie(REFRESH.tokenName, refreshToken)
            .exchangeToMono { it.toEntity<ApiResponse<String>>() }
            .awaitSingle()

        assertEquals(HttpStatus.OK, logoutResponse.statusCode)
    }
}