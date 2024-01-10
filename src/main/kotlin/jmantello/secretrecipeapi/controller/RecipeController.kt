package jmantello.secretrecipeapi.controller

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import jmantello.secretrecipeapi.annotations.CurrentUserEntity
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.transfer.model.RecipeDTO
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.transfer.request.PublishRecipeRequest
import jmantello.secretrecipeapi.transfer.request.PublishReviewRequest
import jmantello.secretrecipeapi.transfer.request.UpdateRecipeRequest
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.ResponseBuilder.respond
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recipes")
class RecipeController(
    private val userService: UserService,
    private val recipeService: RecipeService,
) {
    @GetMapping
    fun getRecipes(): ResponseEntity<ApiResponse<List<RecipeDTO>>> =
        respond(recipeService.findAll())

    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Long): ResponseEntity<ApiResponse<RecipeDTO>> =
        respond(recipeService.findById(id))

    @PostMapping
    fun publishRecipe(@RequestBody request: PublishRecipeRequest, @CurrentUserEntity user: User): ResponseEntity<ApiResponse<RecipeDTO>> =
        respond(recipeService.publish(request, user))

    @PutMapping("/{id}")
    fun updateRecipe(@PathVariable id: Long, @RequestBody request: UpdateRecipeRequest): ResponseEntity<ApiResponse<RecipeDTO>> =
        respond(recipeService.update(id, request))

    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> =
        respond(recipeService.deleteById(id))

    @GetMapping("/search")
    fun searchRecipes(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) ingredients: List<String>?,
        @RequestParam(required = false) tags: List<String>?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortDirection: String?,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) pageSize: Int?,
    ): ResponseEntity<ApiResponse<List<RecipeDTO>>> =
        respond(recipeService.search(title, author, ingredients, tags, sortBy, sortDirection, page, pageSize))

    @GetMapping("/{id}/reviews")
    fun getRecipeReviews(@PathVariable id: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(recipeService.getRecipeReviews(id))

    @PostMapping("/{id}/reviews")
    fun publishRecipeReview(@PathVariable id: Long, @RequestBody request: PublishReviewRequest, @CurrentUserEntity user: User): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(recipeService.publishRecipeReview(id, request, user))

    @PostMapping("/{id}/save")
    fun saveRecipe(@PathVariable id: Long, @CurrentUserEntity user: User): ResponseEntity<ApiResponse<List<RecipeDTO>>> =
        respond(userService.saveRecipe(id, user))

    @PostMapping("/{id}/unsave")
    fun unsaveRecipe(@PathVariable id: Long, @CurrentUserEntity user: User): ResponseEntity<ApiResponse<List<RecipeDTO>>> =
        respond(userService.unsaveRecipe(id, user))
}