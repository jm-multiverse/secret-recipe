package jmantello.secretrecipeapi.controller

import jmantello.secretrecipeapi.ResponseEntity.Companion.badRequest
import jmantello.secretrecipeapi.ResponseEntity.Companion.noContent
import jmantello.secretrecipeapi.ResponseEntity.Companion.notFound
import jmantello.secretrecipeapi.ResponseEntity.Companion.ok
import jmantello.secretrecipeapi.dto.LoginUserRequest
import jmantello.secretrecipeapi.entity.*
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val recipeService: RecipeService,
    private val reviewService: ReviewService
) {
    @GetMapping
    fun getUsers(): ResponseEntity<ApiResponse<List<UserDTO>>> = ok(userService.findAll())

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserDTO>> {
        return when(val result = userService.findById(id)) {
            is Success -> ok(result.data)
            is Error -> notFound(result.message)
        }
    }

    @PostMapping
    fun createUser(@RequestBody dto: LoginUserRequest): ResponseEntity<ApiResponse<String>> =
        badRequest("New users must be registered through the authentication endpoint: 'api/auth/register'.")

    @PutMapping
    fun updateUser(@RequestBody user: UserDTO): ResponseEntity<ApiResponse<UserDTO>> {
        return when(val result = userService.update(user)) {
            is Success -> ok(result.data)
            is Error -> badRequest(result.message)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        userService.deleteById(id)
        return noContent()
    }
    @GetMapping("{id}/published-recipes")
    fun getPublishedRecipes(@PathVariable id: Long): ResponseEntity<ApiResponse<List<RecipeDTO>>> {
        return when(val result = userService.getPublishedRecipes(id)) {
            is Success -> ok(result.data)
            is Error -> notFound(result.message)
        }
    }
    @PostMapping("/{userId}/save-recipe/{recipeId}")
    fun saveRecipe(@PathVariable userId: Long, @PathVariable recipeId: Long): ResponseEntity<ApiResponse<List<RecipeDTO>>> {
        return when(val result = userService.saveRecipeForUser(userId, recipeId)) {
            is Success -> ok(result.data)
            is Error -> notFound(result.message)
        }
    }

    @GetMapping("{id}/saved-recipes")
    fun getSavedRecipes(@PathVariable id: Long): ResponseEntity<ApiResponse<List<RecipeDTO>>> {
        return when(val result = userService.getSavedRecipes(id)) {
            is Success -> ok(result.data)
            is Error -> notFound(result.message)
        }
    }

    @GetMapping("{id}/published-reviews")
    fun getPublishedReviews(@PathVariable id: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> {
        return when(val result = userService.getPublishedReviews(id)) {
            is Success -> ok(result.data)
            is Error -> notFound(result.message)
        }
    }
}