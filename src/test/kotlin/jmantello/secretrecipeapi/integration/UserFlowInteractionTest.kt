package jmantello.secretrecipeapi.integration

import org.junit.jupiter.api.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserFlowInteractionTest {

    @LocalServerPort
    private var port: Int = 0
    private var host: String = "http://localhost"
    private val endpoints: Endpoints by lazy { Endpoints(host, port) }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    val objectMapper = ObjectMapper()

    private lateinit var testUser: User
    private var testUserEmail = "testuser@example.com"
    private var testUserPassword = "testpassword"
    private var testUserDisplayName = "testdisplayname"

    @Test
    @Order(1)
    fun testUserRegistration() {
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
        val testUser: User = objectMapper.readValue(registerResponse.body!!)
        assertEquals(testUserEmail, testUser.email)
        assertEquals(testUserDisplayName, testUser.displayName)

        this.testUser = testUser
    }

    @Test
    fun testUserLogin() {
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
    fun testGetUserById() {
        // ... test getting user by id
    }

    @Test
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
    fun testPublishRecipes() {
        // TODO: Create more than one recipe request

        // Create Recipe Request
        val publisherId = testUser.id
        val title = "Cheese Steak Sandwich"
        val content = "I love cheese, I love steak, and I love sandwiches. This means that a cheese steak sandwich is sure to knock it out of the park. First you take the cheese..."
        val createRecipeRequest = CreateRecipeRequest(
            publisherId,
            title,
            content
        )

        // Post Request
        val requestEntity = HttpEntity(createRecipeRequest)
        val postResponse: ResponseEntity<String> = restTemplate.exchange(
            publishUrl,
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
        assertEquals(HttpStatus.CREATED, postResponse.statusCode)

        // Deserialize Response
        val createdRecipe: Recipe = objectMapper.readValue(postResponse.body!!)
        assertEquals(publisherId, createdRecipe.publisher!!.id)
        assertEquals(title, createdRecipe.title)
        assertEquals(content, createdRecipe.content)
    }

    @Test
    fun testGetPublishedRecipes() {
        val publishedRecipesUrl = endpoints.publishedRecipes(publisherId)
        val getPublishedRecipesResponse: ResponseEntity<MutableList<Recipe>> = restTemplate.getForEntity(
            publishedRecipesUrl,
            mutableListOf<Recipe>()
        )

        val usersRecipes =  getPublishedRecipesResponse.body!!
        // TODO: Test results from getting users recipes.
    }

    @Test
    fun testSaveRecipes() {
        // ... test saving recipes
    }

    @Test
    fun testGetSavedRecipes() {
        // ... test getting user's saved recipes
    }

    @Test
    fun testGetAllRecipes() {
        // ... test getting all recipes
    }

    @Test
    fun testPublishReviews() {
        // ... test publishing reviews
    }

    @Test
    fun testGetPublishedReviews() {
        // ... test getting user's published reviews
    }

    @Test
    fun testFollowAndFollowers() {
        // ... test following another user and getting followers
    }

    @Test
    fun testLikeReview() {
        // ... test liking a review and viewing likes
    }

    @Test
    fun testLogoutAndLogin() {
        // ... test logout and login
    }

    @Test
    fun testDeleteAccount() {
        // ... test deleting user account
    }
}