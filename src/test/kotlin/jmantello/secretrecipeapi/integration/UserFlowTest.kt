package jmantello.secretrecipeapi.integration

import jmantello.secretrecipeapi.dto.*
import jmantello.secretrecipeapi.entity.RecipeDTO
import jmantello.secretrecipeapi.entity.ReviewDTO
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.toEntity
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class UserFlowTest : IntegrationTestBase() {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var reviewService: ReviewService

    // Test User
    private lateinit var testUser: UserDTO
    private var testUserEmail = "testuser@example.com"
    private var testUserPassword = "testpassword"
    private var testUserDisplayName = "testdisplayname"
    private var testUserIsAdmin = false

    // Test User 2
    private lateinit var testUser2: UserDTO
    private var testUser2Email = "testuser2@example.com"
    private var testUser2Password = "testpassword"
    private var testUser2DisplayName = "testdisplayname2"
    private var testUser2IsAdmin = false

    // Test Recipe
    private lateinit var testRecipe: RecipeDTO
    val recipeTitle = "Cheese Steak Sandwich"
    val recipeContent =
        "I love cheese, I love steak, and I love sandwiches. This means that a cheese steak sandwich is sure to knock it out of the park. First you take the cheese..."
    val recipeTags = listOf("cheese", "steak", "sandwich")
    val recipeIsPrivate = false

    // Test Review
    private lateinit var testReview: ReviewDTO
    val reviewTitle = "Meh."
    val reviewContent = "I thought this was going to be great, but..."
    val reviewRating = 3.0

    @AfterAll
    fun teardownClass() {
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
        assertEquals(testUserIsAdmin, userDTO.isAdmin)

        // Set test user
        val result = userService.findById(userDTO.id)
        testUser = when (result) {
            is Success -> result.data
            is Error -> fail(result.message)
        }

        // Register and set test user 2
        val registerRequestBody2 = RegisterUserDTO(
            testUser2Email,
            testUser2Password,
            testUser2DisplayName,
            testUser2IsAdmin
        )

        val response2 = webClient.post()
            .uri(registerUrl)
            .bodyValue(registerRequestBody2)
            .exchangeToMono { it.toEntity<ApiResponse<UserDTO>>() }
            .awaitSingle()

        assertEquals(CREATED, response2.statusCode)

        val apiResponse2 = response2.body ?: fail("Response body was null.")
        val userDTO2 = apiResponse2.data ?: fail("Response body data was null.")

        val result2 = userService.findById(userDTO2.id)
        testUser2 = when (result2) {
            is Success -> result2.data
            is Error -> fail(result2.message)
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

        // Validate response body data
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
        val testUserDisplayName = "test changed display name"

        val request = UpdateUserDTO(
            id = testUser.id,
            displayName = testUserDisplayName
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
        assertEquals(testUserDisplayName, userDTO.displayName)
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
        val recipe = recipes.find { it.id == testRecipe.id } ?: fail("Recipe with id ${testRecipe.id} not found.")
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

        val recipe = recipes.find { it.id == testRecipe.id } ?: fail("Recipe with id ${testRecipe.id} not found.")
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
        val recipe = recipes.find { it.id == testRecipe.id } ?: fail("Recipe with id ${testRecipe.id} not found.")
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

        assertEquals(CREATED, response.statusCode)

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

    @Test
    @Order(11)
    fun testGetPublishedReviews() = runBlocking {
        val publisherId = testUser.id
        val publishedReviewsUrl = endpoints.getPublishedReviews(publisherId)

        val response = webClient.get()
            .uri(publishedReviewsUrl)
            .exchangeToMono { it.toEntity<ApiResponse<List<ReviewDTO>>>() }
            .awaitSingle()

        assertEquals(OK, response.statusCode)

        val apiResponse = response.body ?: fail("Response body was null.")
        val usersReviews = apiResponse.data ?: fail("Response body data was null.")

        val createdReview =
            usersReviews.find { it.id == testReview.id } ?: fail("Review with id ${testReview.id} not found.")
        assertEquals(reviewTitle, createdReview.title)
        assertEquals(reviewContent, createdReview.content)
    }

    @Test
    @Order(11)
    fun testFollowAndFollowers() = runBlocking {

        // testUser2 follows testUser
        val followUrl = endpoints.follow(testUser.id, testUser2.id)
        val followResponse = webClient.post()
            .uri(followUrl)
            .exchangeToMono { it.toEntity<ApiResponse<List<UserDTO>>>() }
            .awaitSingle()

        assertEquals(OK, followResponse.statusCode)

        // Response is the list of users testUser2 is following
        val followApiResponse = followResponse.body ?: fail("Response body was null.")
        val following = followApiResponse.data ?: fail("Response body data was null.")

        val userBeingFollowed =
            following.find { it.id == testUser.id } ?: fail("Follower with id ${testUser.id} not found.")
        assertEquals(testUser.id, userBeingFollowed.id)

        // Get testUser's followers, assert it contains testUser2
        val getFollowersUrl = endpoints.followers(testUser.id)
        val followersResponse = webClient.get()
            .uri(getFollowersUrl)
            .exchangeToMono { it.toEntity<ApiResponse<List<UserDTO>>>() }
            .awaitSingle()

        assertEquals(OK, followersResponse.statusCode)

        val followersApiResponse = followersResponse.body ?: fail("Response body was null.")
        val followersList = followersApiResponse.data ?: fail("Response body data was null.")
        val follower =
            followersList.find { it.id == testUser2.id } ?: fail("Follower with id ${testUser2.id} not found.")
        assertEquals(testUser2.id, follower.id)
    }

    @Test
    @Order(12)
    fun testUnfollow() = runBlocking {
        // testUser2 unfollows testUser
        val unfollowUrl = endpoints.unfollow(testUser.id, testUser2.id)
        val unfollowResponse = webClient.post()
            .uri(unfollowUrl)
            .exchangeToMono { it.toEntity<ApiResponse<List<UserDTO>>>() }
            .awaitSingle()

        assertEquals(OK, unfollowResponse.statusCode)

        // Response is testUser2 following list
        val unfollowApiResponse = unfollowResponse.body ?: fail("Response body was null.")
        val followingList = unfollowApiResponse.data ?: fail("Response body data was null.")
        assertFalse(followingList.any { it.id == testUser.id }, "Follower with id ${testUser.id} found after unfollow.")

        // Get testUser's followers, assert it does not contain testUser2
        val getFollowersUrl = endpoints.followers(testUser.id)
        val followersResponse = webClient.get()
            .uri(getFollowersUrl)
            .exchangeToMono { it.toEntity<ApiResponse<List<UserDTO>>>() }
            .awaitSingle()

        assertEquals(OK, followersResponse.statusCode)

        val followersApiResponse = followersResponse.body ?: fail("Response body was null.")
        val followersList = followersApiResponse.data ?: fail("Response body data was null.")
        assertFalse(followersList.any { it.id == testUser2.id }, "Follower with id ${testUser2.id} found after unfollow.")
    }

    @Test
    @Order(13)
    fun testLikeReview() = runBlocking {
        val likeUrl = endpoints.likeReview(testUser2.id, testReview.id)
        val likeResponse = webClient.post()
            .uri(likeUrl)
            .exchangeToMono { it.toEntity<ApiResponse<List<ReviewDTO>>>() }
            .awaitSingle()

        assertEquals(OK, likeResponse.statusCode)

        val likeApiResponse = likeResponse.body ?: fail("Response body was null.")
        val likedReviews = likeApiResponse.data ?: fail("Response body data was null.")

        val likedReview =
            likedReviews.find { it.id == testReview.id } ?: fail("Liked review with id ${testReview.id} not found.")
        assertEquals(testReview.id, likedReview.id)
    }

    @Test
    @Order(14)
    fun testLogoutAndLogin() = runBlocking {
        // Logout
        val logoutUrl = endpoints.logout
        val logoutResponse = webClient.post()
            .uri(logoutUrl)
            .exchangeToMono { it.toEntity<ApiResponse<String>>() }
            .awaitSingle()

        assertEquals(OK, logoutResponse.statusCode)

        val loginUrl = endpoints.login
        val loginRequestBody = LoginUserDTO(
            testUserEmail,
            testUserPassword
        )

        // Log back in
        val loginResponse = webClient.post()
            .uri(loginUrl)
            .bodyValue(loginRequestBody)
            .exchangeToMono { it.toEntity<ApiResponse<String>>() }
            .awaitSingle()

        assertEquals(OK, loginResponse.statusCode)
    }

    @Test
    @Order(15)
    fun testDeleteAccount() = runBlocking {
        // Delete test user
        val deleteUrl = endpoints.deleteUser(testUser.id)
        val deleteResponse = webClient.delete()
            .uri(deleteUrl)
            .exchangeToMono { it.toEntity<ApiResponse<Any>>() }
            .awaitSingle()

        assertEquals(NO_CONTENT, deleteResponse.statusCode)

        // Delete test user 2
        val deleteUrl2 = endpoints.deleteUser(testUser2.id)
        val deleteResponse2 = webClient.delete()
            .uri(deleteUrl2)
            .exchangeToMono { it.toEntity<ApiResponse<Any>>() }
            .awaitSingle()

        assertEquals(NO_CONTENT, deleteResponse2.statusCode)
    }
}