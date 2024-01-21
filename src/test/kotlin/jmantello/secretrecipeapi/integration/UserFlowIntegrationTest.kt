package jmantello.secretrecipeapi.integration

import jmantello.secretrecipeapi.transfer.model.RecipeDTO
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.transfer.model.UserDTO
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.TokenService.TokenType.ACCESS
import jmantello.secretrecipeapi.service.TokenService.TokenType.REFRESH
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.transfer.request.*
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
import org.springframework.http.ResponseCookie
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.toEntity
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class UserFlowIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var reviewService: ReviewService

    // Access Tokens
    private lateinit var testUserAccessToken: String
    private lateinit var testUserRefreshToken: String

    private lateinit var testUser2AccessToken: String
    private lateinit var testUser2RefreshToken: String

    // Test User
    private lateinit var testUser: UserDTO
    private var testUserEmail = "testuser@example.com"
    private var testUserPassword = "testpassword"
    private var testUserDisplayName = "testdisplayname"

    // Test User 2
    private lateinit var testUser2: UserDTO
    private var testUser2Email = "testuser2@example.com"
    private var testUser2Password = "testpassword"
    private var testUser2DisplayName = "testdisplayname2"

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

        assertEquals(CREATED, response.statusCode)

        val apiResponse = response.body ?: fail("Response body was null.")
        val userDTO = apiResponse.data ?: fail("Response body data was null.")

        assertNotNull(userDTO.id)
        assertEquals(testUserEmail, userDTO.email)
        assertEquals(testUserDisplayName, userDTO.displayName)
        assertContains(userDTO.roles, User.Role.USER)
        assertEquals(User.Status.ACTIVE, userDTO.status)

        // Set test user
        val result = userService.findById(userDTO.id)
        testUser = when (result) {
            is Success -> result.data
            is Error -> fail(result.message)
        }

        // Register and set test user 2
        val registerRequestBody2 = RegisterUserRequest(
            testUser2Email,
            testUser2Password,
            testUser2DisplayName,
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
        // Login testUser
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

        assertEquals(OK, response.statusCode)

        val apiResponse = response.body ?: Assertions.fail("Response body was null.")
        assertNull(apiResponse.error)

        val userDTO = apiResponse.data ?: Assertions.fail("Response body data was null.")
        assertEquals(testUser.id, userDTO.id)

        val accessTokenCookie = cookies!!.getFirst(ACCESS.tokenName) ?: Assertions.fail("Access token cookie was null.")
        assertNotNull(accessTokenCookie.value)
        testUserAccessToken = accessTokenCookie.value

        val refreshTokenCookie =
            cookies!!.getFirst(REFRESH.tokenName) ?: Assertions.fail("Refresh token cookie was null.")
        assertNotNull(refreshTokenCookie.value)
        testUserRefreshToken = refreshTokenCookie.value

        // Login testUser2
        val loginRequestBody2 = UserLoginRequest(
            testUser2Email,
            testUser2Password
        )
        var cookies2: MultiValueMap<String, ResponseCookie>? = null

        val response2 = webClient.post()
            .uri(loginUrl)
            .bodyValue(loginRequestBody2)
            .exchangeToMono {
                cookies2 = it.cookies()
                it.toEntity<ApiResponse<UserDTO>>()
            }
            .awaitSingle()

        assertEquals(OK, response2.statusCode)

        val apiResponse2 = response2.body ?: Assertions.fail("Response body was null.")
        assertNull(apiResponse2.error)

        val userDTO2 = apiResponse2.data ?: Assertions.fail("Response body data was null.")
        assertEquals(testUser2.id, userDTO2.id)

        val accessTokenCookie2 = cookies2!!.getFirst(ACCESS.tokenName) ?: Assertions.fail("Access token cookie was null.")
        assertNotNull(accessTokenCookie2.value)
        testUser2AccessToken = accessTokenCookie2.value

        val refreshTokenCookie2 =
            cookies2!!.getFirst(REFRESH.tokenName) ?: Assertions.fail("Refresh token cookie was null.")
        assertNotNull(refreshTokenCookie2.value)
        testUser2RefreshToken = refreshTokenCookie2.value
    }

    @Test
    @Order(2)
    fun testGetUserById() = runBlocking {

        val getUserUrl = endpoints.getUser(testUser.id)
        val response = webClient.get()
            .uri(getUserUrl)
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
            .exchangeToMono { it.toEntity<ApiResponse<UserDTO>>() }
            .awaitSingle()

        val apiResponse = response.body ?: fail("Response body was null.")
        assertNull(apiResponse.error)

        // Validate response body data
        val userDTO = apiResponse.data ?: fail("Response body data was null.")
        assertEquals(testUser.id, userDTO.id)
        assertEquals(testUser.email, userDTO.email)
        assertEquals(testUser.displayName, userDTO.displayName)
        assertEquals(testUser.dateCreated, userDTO.dateCreated)
        assertEquals(testUser.roles, userDTO.roles)
        assertEquals(testUser.status, userDTO.status)
    }

    @Test
    @Order(4)
    fun testUpdateAccount() = runBlocking {
        // Change Test User
        val updateUrl = endpoints.updateUser(testUser.id)
        val testUserDisplayName = "test changed display name"

        val request = UpdateUserRequest(
            id = testUser.id,
            displayName = testUserDisplayName
        )

        // Put Request
        val response = webClient.put()
            .uri(updateUrl)
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
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
        assertEquals(testUser.dateCreated, userDTO.dateCreated)
        assertEquals(testUser.roles, userDTO.roles)
        assertEquals(testUser.status, userDTO.status)
    }

    @Test
    @Order(5)
    fun testPublishRecipes() = runBlocking {
        // TODO: Create more than one recipe request
        val publishUrl = endpoints.recipes

        // Create Recipe Requests
        val publisherId = testUser.id
        val request = PublishRecipeRequest(
            recipeTitle,
            recipeContent,
            recipeTags,
            recipeIsPrivate,
        )

        // Post Request
        val response = webClient.post()
            .uri(publishUrl)
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
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
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
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
        val recipeId = testRecipe.id
        val saveRecipeUrl = endpoints.saveRecipe(recipeId)

        val request = webClient.post()
            .uri(saveRecipeUrl)
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
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
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
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
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
            .exchangeToMono { it.toEntity<ApiResponse<List<RecipeDTO>>>() }
            .awaitSingle()

        val apiResponse = request.body ?: fail("Response body was null.")
        val recipes = apiResponse.data ?: fail("Response body data was null.")

        assertTrue(recipes.any { it.id == testRecipe.id }, "Recipe with id ${testRecipe.id} not found.")
    }

    @Test
    @Order(10)
    fun testPublishReviews() = runBlocking {
        val publishedReviewUrl = endpoints.publishReview(testRecipe.id)

        // Create Review Requests
        val request = PublishReviewRequest(
            reviewTitle,
            reviewContent,
            reviewRating
        )

        // Post Request
        val response = webClient.post()
            .uri(publishedReviewUrl)
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
            .bodyValue(request)
            .exchangeToMono { it.toEntity<ApiResponse<ReviewDTO>>() }
            .awaitSingle()

        assertEquals(CREATED, response.statusCode)

        // Deserialize Review Response
        val apiResponse = response.body ?: fail("Response body was null.")
        assertNull(apiResponse.error)

        val createdReview = apiResponse.data ?: fail("Response body data was null.")
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
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
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
        // We'll need to send testUser2's access token with the 'follow' request, because the controller determines who's doing the following based on that.

        // testUser2 follows testUser
        val followUrl = endpoints.follow(testUser.id)
        val followResponse = webClient.post()
            .uri(followUrl)
            .cookie(ACCESS.tokenName, testUser2AccessToken)
            .cookie(REFRESH.tokenName, testUser2RefreshToken)
            .exchangeToMono { it.toEntity<ApiResponse<List<UserDTO>>>() }
            .awaitSingle()

        assertEquals(OK, followResponse.statusCode)

        // Response is testUser2's following list
        val followApiResponse = followResponse.body ?: fail("Response body was null.")
        val following = followApiResponse.data ?: fail("Response body data was null.")

        val userBeingFollowed =
            following.find { it.id == testUser.id } ?: fail("Follower with id ${testUser.id} not found.")
        assertEquals(testUser.id, userBeingFollowed.id)

        // Get testUser's followers, assert it contains testUser2
        val getFollowersUrl = endpoints.followers(testUser.id)
        val followersResponse = webClient.get()
            .uri(getFollowersUrl)
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
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
        val unfollowUrl = endpoints.unfollow(testUser.id)
        val unfollowResponse = webClient.post()
            .uri(unfollowUrl)
            .cookie(ACCESS.tokenName, testUser2AccessToken)
            .cookie(REFRESH.tokenName, testUser2RefreshToken)
            .exchangeToMono { it.toEntity<ApiResponse<List<UserDTO>>>() }
            .awaitSingle()

        assertEquals(OK, unfollowResponse.statusCode)

        // Response is testUser2's following list
        val unfollowApiResponse = unfollowResponse.body ?: fail("Response body was null.")
        val followingList = unfollowApiResponse.data ?: fail("Response body data was null.")
        assertFalse(followingList.any { it.id == testUser.id }, "Follower with id ${testUser.id} found after unfollow.")

        // Get testUser's followers, assert it does not contain testUser2
        val getFollowersUrl = endpoints.followers(testUser.id)
        val followersResponse = webClient.get()
            .uri(getFollowersUrl)
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
            .exchangeToMono { it.toEntity<ApiResponse<List<UserDTO>>>() }
            .awaitSingle()

        assertEquals(OK, followersResponse.statusCode)

        val followersApiResponse = followersResponse.body ?: fail("Response body was null.")
        val followersList = followersApiResponse.data ?: fail("Response body data was null.")
        assertFalse(
            followersList.any { it.id == testUser2.id },
            "Follower with id ${testUser2.id} found after unfollow."
        )
    }

    @Test
    @Order(13)
    fun testLikeReview() = runBlocking {
        val likeUrl = endpoints.likeReview(testReview.id)
        val likeResponse = webClient.post()
            .uri(likeUrl)
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
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
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
            .exchangeToMono { it.toEntity<ApiResponse<String>>() }
            .awaitSingle()

        assertEquals(OK, logoutResponse.statusCode)

        val loginUrl = endpoints.login
        val loginRequestBody = UserLoginRequest(
            testUserEmail,
            testUserPassword
        )

        // Log back in
        val loginResponse = webClient.post()
            .uri(loginUrl)
            .bodyValue(loginRequestBody)
            .exchangeToMono { it.toEntity<ApiResponse<UserDTO>>() }
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
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
            .exchangeToMono { it.toEntity<ApiResponse<Any>>() }
            .awaitSingle()

        assertEquals(NO_CONTENT, deleteResponse.statusCode)

        // Delete test user 2
        val deleteUrl2 = endpoints.deleteUser(testUser2.id)
        val deleteResponse2 = webClient.delete()
            .uri(deleteUrl2)
            .cookie(ACCESS.tokenName, testUserAccessToken)
            .cookie(REFRESH.tokenName, testUserRefreshToken)
            .exchangeToMono { it.toEntity<ApiResponse<Any>>() }
            .awaitSingle()

        assertEquals(NO_CONTENT, deleteResponse2.statusCode)
    }
}