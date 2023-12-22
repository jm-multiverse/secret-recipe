package jmantello.secretrecipeapi.integration

import com.fasterxml.jackson.databind.ObjectMapper
import jmantello.secretrecipeapi.dto.*
import jmantello.secretrecipeapi.entity.*
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.Endpoints
import jmantello.secretrecipeapi.util.Result.*
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class UserFlowTest {

    // TODO: Explore using WebClient as a newer alternative to restTemplate

    @LocalServerPort
    private var port: Int = 0
    private var host: String = "http://localhost"
    private val endpoints: Endpoints by lazy { Endpoints(host, port) }

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder
    private lateinit var webClient: WebClient

    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    val objectMapper = ObjectMapper()

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var reviewService: ReviewService

    private lateinit var testUser: UserDTO
    private var testUserEmail = "testuser@example.com"
    private var testUserPassword = "testpassword"
    private var testUserDisplayName = "testdisplayname"
    private var testUserIsAdmin = false

    private lateinit var testRecipe: RecipeDTO
    val recipeTitle = "Cheese Steak Sandwich"
    val recipeContent =
        "I love cheese, I love steak, and I love sandwiches. This means that a cheese steak sandwich is sure to knock it out of the park. First you take the cheese..."
    val recipeTags = listOf("cheese", "steak", "sandwich")
    val recipeIsPrivate = false

    private lateinit var testReview: ReviewDTO
    val reviewTitle = "Meh."
    val reviewContent = "I thought this was going to be great, but..."
    val reviewRating = 3.0

    @BeforeAll
    fun setupClass() {
        val baseUrl = "http://localhost:$port"
        webClient = webClientBuilder.baseUrl(baseUrl).build()
//        testUserRegistration()
//        testUserLogin()
    }

    @AfterAll
    fun teardownClass() {
//        testLogoutAndLogin()
//        testDeleteAccount()
    }

    @Test
    @Order(0)
    fun testUserRegistration(): Unit = runBlocking {

        val registerUrl = endpoints.register
        val registerRequestBody = RegisterUserDTO(
            testUserEmail,
            testUserPassword,
            testUserDisplayName,
            testUserIsAdmin
        )

        val response = webClient.post()
            .uri(registerUrl)
            .bodyValue(registerRequestBody)
            .exchangeToMono { it.toEntity<ApiResponse<UserDTO>>() }
            .awaitSingle()

        assertEquals(CREATED, response.statusCode)

        val apiResponse = response.body ?: fail("Response body was null.")

        val userDTO = apiResponse.data ?: fail("Response body data was null.")
        assertNotNull(userDTO.id)
        assertEquals(testUserEmail, userDTO.email)
        assertEquals(testUserDisplayName, userDTO.displayName)

        // Set test user
        val result = userService.findById(userDTO.id)
        testUser = when (result) {
            is Success -> result.data
            is Error -> fail(result.message)
        }
    }

    @Test
    @Order(1)
    fun testUserLogin(): Unit = runBlocking {

        val loginUrl = endpoints.login
        val loginRequestBody = LoginUserDTO(
            testUserEmail,
            testUserPassword
        )

        val response: ResponseEntity<ApiResponse<String>> = webClient.post()
            .uri(loginUrl)
            .bodyValue(loginRequestBody)
            .exchangeToMono { it.toEntity<ApiResponse<String>>() }
            .awaitSingle()

        assertEquals(OK, response.statusCode)

        val apiResponse = response.body ?: fail("Response body was null.")
        assertNull(apiResponse.error)
    }


    @Test
    @Order(2)
    fun testGetUserById() = runBlocking {

        val getUserUrl = endpoints.getUser(testUser.id)
        val response = webClient.get()
            .uri(getUserUrl)
            .exchangeToMono { it.toEntity<ApiResponse<UserDTO>>() }
            .awaitSingle()

        val apiResponse = response.body ?: fail("Response body was null.")
        assertNull(apiResponse.error)

        val userDTO = apiResponse.data ?: fail("Response body data was null.")
        assertEquals(testUser.id, userDTO.id)
        assertEquals(testUser.email, userDTO.email)
        assertEquals(testUser.displayName, userDTO.displayName)
        assertEquals(testUser.isActive, userDTO.isActive)
        assertEquals(testUser.isAdmin, userDTO.isAdmin)
        assertEquals(testUser.dateCreated, userDTO.dateCreated)
    }

    @Test
    @Order(4)
    fun testUpdateAccount() = runBlocking {
        // Change Test User
        val updateUrl = endpoints.updateUser(testUser.id)
        val changedDisplayName = "test changed display name"

        val request = UpdateUserDTO(
            id = testUser.id,
            displayName = changedDisplayName
        )

        // Put Request
        val response = webClient.put()
            .uri(updateUrl)
            .bodyValue(request)
            .exchangeToMono { it.toEntity<ApiResponse<UserDTO>>() }
            .awaitSingle()

        assertEquals(OK, response.statusCode)

        val apiResponse = response.body ?: fail("Response body was null.")
        assertNull(apiResponse.error)

        val userDTO = apiResponse.data ?: fail("Response body data was null.")
        assertEquals(testUser.id, userDTO.id)
        assertEquals(testUser.email, userDTO.email)
        assertEquals(changedDisplayName, userDTO.displayName)
        assertEquals(testUser.isActive, userDTO.isActive)
        assertEquals(testUser.isAdmin, userDTO.isAdmin)
        assertEquals(testUser.dateCreated, userDTO.dateCreated)
    }

    @Test
    @Order(5)
    fun testPublishRecipes() = runBlocking {
        // TODO: Create more than one recipe request
        val publishUrl = endpoints.recipes

        // Create Recipe Requests
        val publisherId = testUser.id
        val request = PublishRecipeDTO(
            publisherId,
            recipeTitle,
            recipeContent,
            recipeTags,
            recipeIsPrivate,
        )

        // Post Request
        val response = webClient.post()
            .uri(publishUrl)
            .bodyValue(request)
            .exchangeToMono { it.toEntity<ApiResponse<RecipeDTO>>() }
            .awaitSingle()

        assertEquals(CREATED, response.statusCode)

        val apiResponse = response.body ?: fail("Response body was null.")

        val recipe = apiResponse.data ?: fail("Response body data was null.")

        assertEquals(publisherId, recipe.publisherId)
        assertEquals(recipeTitle, recipe.title)
        assertEquals(recipeContent, recipe.content)
        assertEquals(recipeTags, recipe.tags)
        assertEquals(recipeIsPrivate, recipe.isPrivate)

        // Set test recipe
        val result = recipeService.findById(recipe.id)
        testRecipe = when (result) {
            is Success -> result.data
            is Error -> fail(result.message)
        }
    }

    @Test
    @Order(6)
    fun testGetPublishedRecipes() = runBlocking {
        val publisherId = testUser.id
        val publishedRecipesUrl = endpoints.getPublishedRecipes(publisherId)

        val response = webClient.get()
            .uri(publishedRecipesUrl)
            .exchangeToMono { it.toEntity<ApiResponse<List<RecipeDTO>>>() }
            .awaitSingle()

        val apiResponse = response.body ?: fail("Response body was null.")

        val recipes = apiResponse.data ?: fail("Response body data was null.")
        val recipe = recipes[0]
        assertEquals(recipeTitle, recipe.title)
        assertEquals(recipeContent, recipe.content)
    }

    @Test
    @Order(7)
    fun testSaveRecipes() = runBlocking {
        val userId = testUser.id
        val recipeId = testRecipe.id
        val saveRecipeUrl = endpoints.saveRecipe(userId, recipeId)

        val request = webClient.post()
            .uri(saveRecipeUrl)
            .exchangeToMono { it.toEntity<ApiResponse<List<RecipeDTO>>>() }
            .awaitSingle()

        val apiResponse = request.body ?: fail("Response body was null.")
        val recipes = apiResponse.data ?: fail("Response body data was null.")
    }

    @Test
    @Order(8)
    fun testGetSavedRecipes() = runBlocking {
        val userId = testUser.id
        val savedRecipesUrl = endpoints.getSavedRecipes(userId)

        val response = webClient.get()
            .uri(savedRecipesUrl)
            .exchangeToMono { it.toEntity<ApiResponse<List<RecipeDTO>>>() }
            .awaitSingle()

        val apiResponse = response.body ?: fail("Response body was null.")
        val recipes = apiResponse.data ?: fail("Response body data was null.")
        val recipe = recipes[0]
        assertEquals(recipeTitle, recipe.title)
        assertEquals(recipeContent, recipe.content)
    }

    @Test
    @Order(9)
    fun testGetAllRecipes() = runBlocking {
        val getRecipesUrl = endpoints.recipes

        val request = webClient.get()
            .uri(getRecipesUrl)
            .exchangeToMono { it.toEntity<ApiResponse<List<RecipeDTO>>>() }
            .awaitSingle()

        val apiResponse = request.body ?: fail("Response body was null.")
        val recipes = apiResponse.data ?: fail("Response body data was null.")

        assertTrue(recipes.any { it.id == testRecipe.id }, "Recipe with id ${testRecipe.id} not found.")
    }

    @Test
    @Order(10)
    fun testPublishReviews() = runBlocking {
        val publisherId = testUser.id
        val publishedReviewUrl = endpoints.reviews

        // Create Review Requests
        val request = PublishReviewDTO(
            publisherId,
            testRecipe.id,
            reviewTitle,
            reviewContent,
            reviewRating
        )

        // Post Request
        val response = webClient.post()
            .uri(publishedReviewUrl)
            .bodyValue(request)
            .exchangeToMono { it.toEntity<ApiResponse<ReviewDTO>>() }
            .awaitSingle()

        // Deserialize Review Response
        val apiResponse = response.body ?: fail("Response body was null.")
        assertNull(apiResponse.error)

        val createdReview = apiResponse.data ?: fail("Response body data was null.")
        assertEquals(publisherId, createdReview.publisherId)
        assertEquals(testRecipe.id, createdReview.recipeId)
        assertEquals(reviewTitle, createdReview.title)
        assertEquals(reviewContent, createdReview.content)
        assertEquals(reviewRating, createdReview.rating)

        // Set test review
        val result = reviewService.findById(createdReview.id)
        testReview = when (result) {
            is Success -> result.data
            is Error -> fail(result.message)
        }
    }

//    @Test
//    @Order(10)
//    fun testGetPublishedReviews() {
//        val publisherId = testUser.id
//        val publishedReviewsUrl = endpoints.getPublishedReviews(publisherId)
//
//        val requestEntity = HttpEntity.EMPTY
//        val responseType = object : ParameterizedTypeReference<List<Review>>() {}
//
//        val getPublishedReviewsResponse: ResponseEntity<List<Review>> = restTemplate.exchange(
//            publishedReviewsUrl,
//            HttpMethod.GET,
//            requestEntity,
//            responseType
//        )
//
//        val usersReviews = getPublishedReviewsResponse.body ?: fail("Response body from getting published reviews was supposed to contain recipes, but was null.")
//        val createdReview = usersReviews[0]
//        assertEquals(reviewTitle, createdReview.title)
//        assertEquals(reviewContent, createdReview.content)
//    }
//
//    @Test
//    @Order(11)
//    fun testFollowAndFollowers() {
//        // ... test following another user and getting followers
//    }
//
//    @Test
//    @Order(12)
//    fun testLikeReview() {
//        // ... test liking a review and viewing likes
//    }
//
//    private fun testLogoutAndLogin() {
//        // ... test logout and login
//    }
//
//    private fun testDeleteAccount() {
//        // ... test deleting user account
//    }
}