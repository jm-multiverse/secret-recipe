package jmantello.secretrecipeapi.controller

import jmantello.secretrecipeapi.transfer.request.UserLoginRequest
import jmantello.secretrecipeapi.transfer.request.UpdateUserRequest
import jmantello.secretrecipeapi.transfer.model.RecipeDTO
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.transfer.model.UserDTO
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
) {
    @GetMapping
    fun getUsers(): ResponseEntity<ApiResponse<List<UserDTO>>> =
        respond(userService.findAll())

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserDTO>> =
        respond(userService.findById(id))

    @PostMapping
    fun createUser(@RequestBody dto: UserLoginRequest): ResponseEntity<ApiResponse<String>> =
        respond(Error("New users must be registered through the authentication endpoint: 'api/auth/register'."))

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody userDTO: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserDTO>> =
        respond(userService.update(id, userDTO))

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<Any>> =
        respond(userService.deleteById(id))

    @GetMapping("{id}/published-recipes")
    fun getPublishedRecipes(@PathVariable id: Long): ResponseEntity<ApiResponse<List<RecipeDTO>>> =
        respond(userService.getPublishedRecipes(id))

    @GetMapping("{id}/saved-recipes")
    fun getSavedRecipes(@PathVariable id: Long): ResponseEntity<ApiResponse<List<RecipeDTO>>> =
        respond(userService.getSavedRecipes(id))

    @GetMapping("{id}/published-reviews")
    fun getPublishedReviews(@PathVariable id: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(userService.getPublishedReviews(id))

    @GetMapping("{id}/liked-reviews")
    fun getLikedReviews(@PathVariable id: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(userService.getLikedReviews(id))

    @GetMapping("{id}/following")
    fun following(@PathVariable id: Long): ResponseEntity<ApiResponse<List<UserDTO>>> =
        respond(userService.getFollowing(id))

    @GetMapping("{id}/followers")
    fun followers(@PathVariable id: Long): ResponseEntity<ApiResponse<List<UserDTO>>> =
        respond(userService.getFollowers(id))

    @PostMapping("{userId}/follow/{targetUserId}")
    fun follow(
        @PathVariable userId: Long,
        @PathVariable targetUserId: Long
    ): ResponseEntity<ApiResponse<List<UserDTO>>> =
        respond(userService.follow(userId, targetUserId))

    @PostMapping("{userId}/unfollow/{targetUserId}")
    fun unfollow(
        @PathVariable userId: Long,
        @PathVariable targetUserId: Long
    ): ResponseEntity<ApiResponse<List<UserDTO>>> =
        respond(userService.unfollow(userId, targetUserId))
}