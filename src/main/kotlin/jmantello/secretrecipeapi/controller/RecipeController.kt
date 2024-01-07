package jmantello.secretrecipeapi.controller

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import jakarta.servlet.http.HttpServletRequest
import jmantello.secretrecipeapi.service.AuthenticationService
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.transfer.model.RecipeDTO
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.transfer.request.PublishRecipeRequest
import jmantello.secretrecipeapi.transfer.request.PublishReviewRequest
import jmantello.secretrecipeapi.transfer.request.UpdateRecipeRequest
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.ErrorResponses
import jmantello.secretrecipeapi.util.ResponseBuilder.respond
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recipes")
class RecipeController(
    private val meterRegistry: MeterRegistry,
    private val userService: UserService,
    private val recipeService: RecipeService,
    private val authenticationService: AuthenticationService,
) {
    // Define custom metrics
    val requestsCounter: Counter = Counter.builder("requests.count")
        .tag("controller", "recipe")
        .register(meterRegistry)

    val processingTime: Timer = Timer.builder("requests.processing.time")
        .tag("controller", "recipe")
        .register(meterRegistry)

    @GetMapping
    fun getRecipes(): ResponseEntity<ApiResponse<List<RecipeDTO>>> =
        respond(recipeService.findAll())

    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Long): ResponseEntity<ApiResponse<RecipeDTO>> =
        respond(recipeService.findById(id))

    @PostMapping
    fun publishRecipe(@RequestBody request: PublishRecipeRequest): ResponseEntity<ApiResponse<RecipeDTO>> =
        respond(recipeService.publish(request))

    @PutMapping("/{id}")
    fun updateRecipe(
        @PathVariable id: Long,
        @RequestBody request: UpdateRecipeRequest
    ): ResponseEntity<ApiResponse<RecipeDTO>> =
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

    @PostMapping("/{recipeId}/reviews")
    fun publishRecipeReview(@PathVariable recipeId: Long, @RequestBody request: PublishReviewRequest): ResponseEntity<ApiResponse<ReviewDTO>> {
        val currentUserId = when (val authenticationResult = authenticationService.getCurrentUserId()) {
            is Success -> authenticationResult.data
            is Error -> return respond(ErrorResponses.unauthorizedError)
        }

        return respond(recipeService.publishRecipeReview(recipeId, request))
    }

    @PostMapping("/{id}/save")
    fun saveRecipe(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<ApiResponse<List<RecipeDTO>>> {
        val user = when (val result = authenticationService.getCurrentUserDTO()) {
            is Success -> result.data
            is Error -> return respond(Error(result.message))
        }

        return respond(userService.saveRecipe(user.id, id))
    }

    @PostMapping("/{id}/unsave")
    fun unsaveRecipe(
        @PathVariable id: Long,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<List<RecipeDTO>>> {
        val user = when (val result = authenticationService.getCurrentUserDTO()) {
            is Success -> result.data
            is Error -> return respond(Error(result.message))
        }

        return respond(userService.unsaveRecipe(user.id, id))
    }
}