package jmantello.secretrecipeapi.controller

import jmantello.secretrecipeapi.dto.LoginUserRequest
import jmantello.secretrecipeapi.dto.UpdateUserRequest
import jmantello.secretrecipeapi.entity.RecipeDTO
import jmantello.secretrecipeapi.entity.ReviewDTO
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.ResponseBuilder.respond
import jmantello.secretrecipeapi.util.Result.Error
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val recipeService: RecipeService,
    private val reviewService: ReviewService
) {
    @GetMapping
    fun getUsers(): ResponseEntity<ApiResponse<List<UserDTO>>> = respond(userService.findAll())

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserDTO>> = respond(userService.findById(id))

    @PostMapping
    fun createUser(@RequestBody dto: LoginUserRequest): ResponseEntity<ApiResponse<String>> =
        respond(Error("New users must be registered through the authentication endpoint: 'api/auth/register'."))

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable userId: Long,
        @Valid @RequestBody userDTO: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserDTO>> =
        respond(userService.update(userId, userDTO))

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<Any>> = respond(userService.deleteById(id))

    @GetMapping("{id}/published-recipes")
    fun getPublishedRecipes(@PathVariable id: Long): ResponseEntity<ApiResponse<List<RecipeDTO>>> =
        respond(userService.getPublishedRecipes(id))

    @PostMapping("/{userId}/save-recipe/{recipeId}")
    fun saveRecipe(
        @PathVariable userId: Long,
        @PathVariable recipeId: Long
    ): ResponseEntity<ApiResponse<List<RecipeDTO>>> = respond(userService.saveRecipeForUser(userId, recipeId))

    @GetMapping("{id}/saved-recipes")
    fun getSavedRecipes(@PathVariable id: Long): ResponseEntity<ApiResponse<List<RecipeDTO>>> =
        respond(userService.getSavedRecipes(id))

    @GetMapping("{id}/published-reviews")
    fun getPublishedReviews(@PathVariable id: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(userService.getPublishedReviews(id))
}