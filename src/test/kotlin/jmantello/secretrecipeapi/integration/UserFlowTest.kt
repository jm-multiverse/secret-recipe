package jmantello.secretrecipeapi.integration

import com.fasterxml.jackson.databind.ObjectMapper
import jmantello.secretrecipeapi.dto.LoginUserDTO
import jmantello.secretrecipeapi.dto.RegisterUserDTO
import jmantello.secretrecipeapi.entity.*
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.Endpoints
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    private lateinit var testUser: User
    private var testUserEmail = "testuser@example.com"
    private var testUserPassword = "testpassword"
    private var testUserDisplayName = "testdisplayname"

    private lateinit var testRecipe: Recipe
    val recipeTitle = "Cheese Steak Sandwich"
    val recipeContent =
        "I love cheese, I love steak, and I love sandwiches. This means that a cheese steak sandwich is sure to knock it out of the park. First you take the cheese..."

    private lateinit var testReview: Review
    val reviewTitle = "Meh."
    val reviewContent = "I thought this was going to be great, but..."
    val reviewRating = 3.0

    @BeforeAll
    fun setupClass() {
        val baseUrl = "http://localhost:$port"
        webClient = webClientBuilder.baseUrl(baseUrl).build()
        testUserRegistration()
        testUserLogin()
    }

    @AfterAll
    fun teardownClass() {
//        testLogoutAndLogin()
//        testDeleteAccount()
    }

    private fun testUserRegistration() = runBlocking {
        // Post Request
        val registerUrl = endpoints.register
        val registerRequestBody = RegisterUserDTO(
            testUserEmail,
            testUserPassword,
            testUserDisplayName
        )

        val response: ResponseEntity<ApiResponse<UserDTO>> = webClient.post()
            .uri(registerUrl)
            .bodyValue(registerRequestBody)
            .retrieve()
            .awaitBody<ResponseEntity<ApiResponse<UserDTO>>>()

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)

        val userDTO = response.body!!.data!!
        assertEquals(testUserEmail, userDTO.email)
        assertEquals(testUserDisplayName, userDTO.displayName)
    }

    private fun testUserLogin() = runBlocking {
        // Post Request
        val loginUrl = endpoints.login
        val loginRequestBody = LoginUserDTO(
            testUserEmail,
            testUserPassword
        )

        val response: ResponseEntity<ApiResponse<UserDTO>> = webClient.post()
            .uri(loginUrl)
            .bodyValue(loginRequestBody)
            .retrieve()
            .awaitBody<ResponseEntity<ApiResponse<UserDTO>>>()

        assertEquals(HttpStatus.OK, response.statusCode)
    }


//    @Test
//    @Order(1)
//    fun testGetUserById() {
//        val userId = testUser.id
//        val getUserUrl = endpoints.getUser(userId)
//        val getUserResponse: ResponseEntity<UserResponseDTO> = restTemplate.getForEntity(
//            getUserUrl,
//            HttpMethod.GET,
//            UserResponseDTO::class
//        )
//        val userResponse = getUserResponse.body ?: fail("Response body from getUser was to contain User but was null.")
//        assertEquals(userId, userResponse.id)
//        assertEquals(testUser.email, userResponse.email)
//        assertEquals(testUser.displayName, userResponse.displayName)
//        assertEquals(testUser.isActive, userResponse.isActive)
//        assertEquals(testUser.isAdmin, userResponse.isAdmin)
//        assertEquals(testUser.dateCreated, userResponse.dateCreated)
//    }
//
//    @Test
//    @Order(2)
//    fun testUpdateAccount() {
//        // Change Test User
//        val updateUrl = endpoints.users
//        val changedDisplayName = "test changed display name"
//
//        testUser.displayName = changedDisplayName
//
//        // Put Request
//        val requestEntity = HttpEntity(testUser)
//        val updateResponse: ResponseEntity<String> = restTemplate.exchange(
//            updateUrl,
//            HttpMethod.PUT,
//            requestEntity,
//            String::class.java
//        )
//        assertEquals(HttpStatus.OK, updateResponse.statusCode)
//
//        // Deserialize response
//        val changedUser: User = objectMapper.readValue(updateResponse.body!!)
//        assertEquals(changedDisplayName, changedUser.displayName)
//    }
//
//    @Test
//    @Order(3)
//    fun testPublishRecipes() {
//        // TODO: Create more than one recipe request
//        val publishUrl = endpoints.recipes
//
//        // Create Recipe Requests
//        val publisherId = testUser.id
//        val createRecipeRequest = CreateRecipeRequest(
//            publisherId,
//            recipeTitle,
//            recipeContent
//        )
//
//        // Post Request
//        val requestEntity = HttpEntity(createRecipeRequest)
//        val postResponse: ResponseEntity<RecipeResponse> = restTemplate.exchange(
//            publishUrl,
//            HttpMethod.POST,
//            requestEntity,
//            RecipeResponse::class
//        )
//        assertEquals(HttpStatus.CREATED, postResponse.statusCode)
//
//        // Deserialize Response
//        val createdRecipe = postResponse.body ?: fail("Response body was supposed to contain a newly created recipe, but was null.")
//        assertEquals(publisherId, createdRecipe.publisherId)
//        assertEquals(recipeTitle, createdRecipe.title)
//        assertEquals(recipeContent, createdRecipe.content)
//
//        testRecipe = recipeService.findByIdOrNull(createdRecipe.id) ?: fail("Recipe was not found by recipe service.")
//    }
//
//    @Test
//    @Order(4)
//    fun testGetPublishedRecipes() {
//        val publisherId = testUser.id
//        val publishedRecipesUrl = endpoints.getPublishedRecipes(publisherId)
//
//        val requestEntity = HttpEntity.EMPTY
//        val responseType = object : ParameterizedTypeReference<List<Recipe>>() {}
//
//        val getPublishedRecipesResponse: ResponseEntity<List<Recipe>> = restTemplate.exchange(
//            publishedRecipesUrl,
//            HttpMethod.GET,
//            requestEntity,
//            responseType
//        )
//
//        val usersRecipes = getPublishedRecipesResponse.body ?: fail("Response body from getting published recipes was supposed to contain recipes, but was null.")
//        val createdRecipe = usersRecipes[0]
//        assertEquals(recipeTitle, createdRecipe.title)
//        assertEquals(recipeContent, createdRecipe.content)
//    }
//
//    @Test
//    @Order(5)
//    fun testSaveRecipes() {
//        val userId = testUser.id
//        val recipeId = testRecipe.id
//        val saveRecipeUrl = endpoints.saveRecipe(userId, recipeId)
//
//        val requestEntity = HttpEntity.EMPTY
//
//        // Post Request
//        val postResponse: ResponseEntity<RecipeResponse> = restTemplate.exchange(
//            saveRecipeUrl,
//            HttpMethod.POST,
//            requestEntity,
//            RecipeResponse::class
//        )
//        assertEquals(HttpStatus.OK, postResponse.statusCode)
//    }
//
//    @Test
//    @Order(6)
//    fun testGetSavedRecipes() {
//        val userId = testUser.id
//        val savedRecipesUrl = endpoints.getSavedRecipes(userId)
//
//        val requestEntity = HttpEntity.EMPTY
//        val responseType = object : ParameterizedTypeReference<List<Recipe>>() {}
//
//        val getSavedRecipesResponse: ResponseEntity<List<Recipe>> = restTemplate.exchange(
//            savedRecipesUrl,
//            HttpMethod.GET,
//            requestEntity,
//            responseType
//        )
//
//        val usersSavedRecipes = getSavedRecipesResponse.body ?: fail("Response body from getting saved recipes was supposed to contain recipes, but was null.")
//        val savedRecipe = usersSavedRecipes[0]
//        assertEquals(testRecipe, savedRecipe) // May need to compare prop
//    }
//
//    @Test
//    @Order(7)
//    fun testGetAllRecipes() {
//        val getRecipesUrl = endpoints.recipes
//        val requestEntity = HttpEntity.EMPTY
//        val responseType = object : ParameterizedTypeReference<List<Recipe>>() {}
//
//        val getPublishedRecipesResponse: ResponseEntity<List<Recipe>> = restTemplate.exchange(
//            getRecipesUrl,
//            HttpMethod.GET,
//            requestEntity,
//            responseType
//        )
//
//        val recipes = getPublishedRecipesResponse.body ?: fail("Response body from getting all recipes was supposed to contain recipes, but was null.")
//        val recipe = recipes[0]
//        assertEquals(testRecipe.id, recipe.id)
//        assertEquals(testRecipe.title, recipe.title)
//        assertEquals(testRecipe.content, recipe.content)
//
//    }
//
//    @Test
//    @Order(8)
//    fun testPublishReviews() {
//        val publisherId = testUser.id
//        val publishedReviewUrl = endpoints.reviews
//
//        // Create Review Requests
//        val createReviewRequest = PublishReviewRequest(
//            publisherId,
//            reviewTitle,
//            reviewContent,
//            reviewRating
//        )
//
//        // Post Request
//        val requestEntity = HttpEntity(createReviewRequest)
//        val postResponse: ResponseEntity<ReviewResponse> = restTemplate.exchange(
//            publishedReviewUrl,
//            HttpMethod.POST,
//            requestEntity,
//            ReviewResponse::class
//        )
//        assertEquals(HttpStatus.CREATED, postResponse.statusCode)
//
//        // Deserialize Review Response
//        val createdReview = postResponse.body ?: fail("Response body was supposed to contain a newly created recipe, but was null.")
//        assertEquals(publisherId, createdReview.publisherId)
//        assertEquals(reviewTitle, createdReview.title)
//        assertEquals(reviewContent, createdReview.content)
//        assertEquals(reviewRating, createdReview.rating)
//
//        testReview = reviewService.findByIdOrNull(createdReview.id) ?: fail("Review was not found by review service.")
//    }
//
//    @Test
//    @Order(9)
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
//    @Order(10)
//    fun testFollowAndFollowers() {
//        // ... test following another user and getting followers
//    }
//
//    @Test
//    @Order(11)
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