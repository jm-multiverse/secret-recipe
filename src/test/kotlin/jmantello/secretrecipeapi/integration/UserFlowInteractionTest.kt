package jmantello.secretrecipeapi.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jmantello.secretrecipeapi.entity.*
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.util.Endpoints
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserFlowInteractionTest{

    // TODO: Explore using WebClient as a newer alternative to restTemplate

    @LocalServerPort
    private var port: Int = 0
    private var host: String = "http://localhost"
    private val endpoints: Endpoints by lazy { Endpoints(host, port) }

    @Autowired private lateinit var restTemplate: TestRestTemplate
    val objectMapper = ObjectMapper()

    @Autowired private lateinit var userService: UserService
    @Autowired private lateinit var recipeService: RecipeService
    @Autowired private lateinit var reviewService: ReviewService

    private lateinit var testUser: User
    private var testUserEmail = "testuser@example.com"
    private var testUserPassword = "testpassword"
    private var testUserDisplayName = "testdisplayname"

    private lateinit var testRecipe: Recipe
    val recipeTitle = "Cheese Steak Sandwich"
    val recipeContent = "I love cheese, I love steak, and I love sandwiches. This means that a cheese steak sandwich is sure to knock it out of the park. First you take the cheese..."

    private lateinit var testReview: Review
    val reviewTitle = "Meh."
    val reviewContent = "I thought this was going to be great, but..."
    val reviewRating = 3.0

    @BeforeAll
    fun setupClass() {

        testUserRegistration()
        testUserLogin()
    }

    @AfterAll
    fun teardownClass() {
        testLogoutAndLogin()
        testDeleteAccount()
    }

    private fun testUserRegistration() {
        // Post Request
        val registerUrl = endpoints.register
        val registerRequestBody = RegisterUserDTO(
            testUserEmail,
            testUserPassword,
            testUserDisplayName
        )
        val registerResponse: ResponseEntity<String> = restTemplate.postForEntity(registerUrl, registerRequestBody, String::class.java)
        assertEquals(HttpStatus.CREATED, registerResponse.statusCode)

        // Deserialize User
        testUser = objectMapper.readValue(registerResponse.body!!)
        assertEquals(testUserEmail, testUser.email)
        assertEquals(testUserDisplayName, testUser.displayName)
    }

    private fun testUserLogin() {
        // Post Request
        val loginUrl = endpoints.login
        val loginRequestBody = LoginUserDTO(
            testUserEmail,
            testUserPassword
        )
        val loginResponse: ResponseEntity<String> = restTemplate.postForEntity(loginUrl, loginRequestBody, String::class.java)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)
    }

    @Test
    @Order(3)
    fun testGetUserById() {
        val userId = testUser.id
        val getUserUrl = endpoints.getUser(userId)
        val getUserResponse: ResponseEntity<UserResponseDTO> = restTemplate.getForEntity(
            getUserUrl,
            HttpMethod.GET,
            UserResponseDTO::class
        )
        val userResponse = getUserResponse.body ?: fail("Response body from getUser was to contain User but was null.")
        assertEquals(userId, userResponse.id)
        assertEquals(testUser.email, userResponse.email)
        assertEquals(testUser.displayName, userResponse.displayName)
        assertEquals(testUser.isActive, userResponse.isActive)
        assertEquals(testUser.isAdmin, userResponse.isAdmin)
        assertEquals(testUser.dateCreated, userResponse.dateCreated)
    }

    @Test
    @Order(4)
    fun testUpdateAccount() {
        // Change Test User
        val updateUrl = endpoints.users
        val changedDisplayName = "test changed display name"

        testUser.displayName = changedDisplayName

        // Put Request
        val requestEntity = HttpEntity(testUser)
        val updateResponse: ResponseEntity<String> = restTemplate.exchange(
            updateUrl,
            HttpMethod.PUT,
            requestEntity,
            String::class.java
        )
        assertEquals(HttpStatus.OK, updateResponse.statusCode)

        // Deserialize response
        val changedUser: User = objectMapper.readValue(updateResponse.body!!)
        assertEquals(changedDisplayName, changedUser.displayName)
    }

    @Test
    @Order(5)
    fun testPublishRecipes() {
        // TODO: Create more than one recipe request
        val publishUrl = endpoints.recipes

        // Create Recipe Requests
        val publisherId = testUser.id
        val createRecipeRequest = CreateRecipeRequest(
            publisherId,
            recipeTitle,
            recipeContent
        )

        // Post Request
        val requestEntity = HttpEntity(createRecipeRequest)
        val postResponse: ResponseEntity<RecipeResponse> = restTemplate.exchange(
            publishUrl,
            HttpMethod.POST,
            requestEntity,
            RecipeResponse::class
        )
        assertEquals(HttpStatus.CREATED, postResponse.statusCode)

        // Deserialize Response
        val createdRecipe = postResponse.body ?: fail("Response body was supposed to contain a newly created recipe, but was null.")
        assertEquals(publisherId, createdRecipe.publisher!!.id)
        assertEquals(recipeTitle, createdRecipe.title)
        assertEquals(recipeContent, createdRecipe.content)

        testRecipe = recipeService.findByIdOrNull(createdRecipe.id) ?: fail("Recipe was not found by recipe service.")
    }

    @Test
    @Order(6)
    fun testGetPublishedRecipes() {
        val publisherId = testUser.id
        val publishedRecipesUrl = endpoints.getPublishedRecipes(publisherId)

        val requestEntity = HttpEntity.EMPTY
        val responseType = object : ParameterizedTypeReference<List<Recipe>>() {}

        val getPublishedRecipesResponse: ResponseEntity<List<Recipe>> = restTemplate.exchange(
            publishedRecipesUrl,
            HttpMethod.GET,
            requestEntity,
            responseType
        )

        val usersRecipes = getPublishedRecipesResponse.body ?: fail("Response body from getting published recipes was supposed to contain recipes, but was null.")
        val createdRecipe = usersRecipes[0]
        assertEquals(recipeTitle, createdRecipe.title)
        assertEquals(recipeContent, createdRecipe.content)
    }

    @Test
    @Order(7)
    fun testSaveRecipes() {
        val userId = testUser.id
        val recipeId = testRecipe.id
        val saveRecipeUrl = endpoints.saveRecipe(userId, recipeId)

        val requestEntity = HttpEntity.EMPTY

        // Post Request
        val postResponse: ResponseEntity<RecipeResponse> = restTemplate.exchange(
            saveRecipeUrl,
            HttpMethod.POST,
            requestEntity,
            RecipeResponse::class
        )
        assertEquals(HttpStatus.OK, postResponse.statusCode)
    }

    @Test
    @Order(8)
    fun testGetSavedRecipes() {
        val userId = testUser.id
        val publishedRecipesUrl = endpoints.getSavedRecipes(userId)

        val requestEntity = HttpEntity.EMPTY
        val responseType = object : ParameterizedTypeReference<List<Recipe>>() {}

        val getPublishedRecipesResponse: ResponseEntity<List<Recipe>> = restTemplate.exchange(
            publishedRecipesUrl,
            HttpMethod.GET,
            requestEntity,
            responseType
        )

        val usersSavedRecipes = getPublishedRecipesResponse.body ?: fail("Response body from getting saved recipes was supposed to contain recipes, but was null.")
        val savedRecipe = usersSavedRecipes[0]
        assertEquals(testRecipe, savedRecipe)
    }

    @Test
    @Order(9)
    fun testGetAllRecipes() {
        val getRecipesUrl = endpoints.recipes
        val requestEntity = HttpEntity.EMPTY
        val responseType = object : ParameterizedTypeReference<List<Recipe>>() {}

        val getPublishedRecipesResponse: ResponseEntity<List<Recipe>> = restTemplate.exchange(
            getRecipesUrl,
            HttpMethod.GET,
            requestEntity,
            responseType
        )

        val recipes = getPublishedRecipesResponse.body ?: fail("Response body from getting all recipes was supposed to contain recipes, but was null.")
        val recipe = recipes[0]
        assertEquals(testRecipe.id, recipe.id)
        assertEquals(testRecipe.title, recipe.title)
        assertEquals(testRecipe.content, recipe.content)

    }

    @Test
    @Order(10)
    fun testPublishReviews() {
        val publisherId = testUser.id
        val publishedReviewUrl = endpoints.reviews

        // Create Review Requests
        val createReviewRequest = PublishReviewRequest(
            publisherId,
            reviewTitle,
            reviewContent,
            reviewRating
        )

        // Post Request
        val requestEntity = HttpEntity(createReviewRequest)
        val postResponse: ResponseEntity<ReviewResponse> = restTemplate.exchange(
            publishedReviewUrl,
            HttpMethod.POST,
            requestEntity,
            ReviewResponse::class
        )
        assertEquals(HttpStatus.CREATED, postResponse.statusCode)

        // Deserialize Review Response
        val createdReview = postResponse.body ?: fail("Response body was supposed to contain a newly created recipe, but was null.")
        assertEquals(publisherId, createdReview.publisher!!.id)
        assertEquals(reviewTitle, createdReview.title)
        assertEquals(reviewContent, createdReview.content)
        assertEquals(reviewRating, createdReview.rating)

        testReview = reviewService.findByIdOrNull(createdReview.id) ?: fail("Review was not found by review service.")
    }

    @Test
    @Order(11)
    fun testGetPublishedReviews() {
        val publisherId = testUser.id
        val publishedReviewsUrl = endpoints.getPublishedReviews(publisherId)

        val requestEntity = HttpEntity.EMPTY
        val responseType = object : ParameterizedTypeReference<List<Review>>() {}

        val getPublishedReviewsResponse: ResponseEntity<List<Review>> = restTemplate.exchange(
            publishedReviewsUrl,
            HttpMethod.GET,
            requestEntity,
            responseType
        )

        val usersReviews = getPublishedReviewsResponse.body ?: fail("Response body from getting published reviews was supposed to contain recipes, but was null.")
        val createdReview = usersReviews[0]
        assertEquals(reviewTitle, createdReview.title)
        assertEquals(reviewContent, createdReview.content)
    }

    @Test
    @Order(12)
    fun testFollowAndFollowers() {
        // ... test following another user and getting followers
    }

    @Test
    @Order(13)
    fun testLikeReview() {
        // ... test liking a review and viewing likes
    }

    private fun testLogoutAndLogin() {
        // ... test logout and login
    }

    private fun testDeleteAccount() {
        // ... test deleting user account
    }
}