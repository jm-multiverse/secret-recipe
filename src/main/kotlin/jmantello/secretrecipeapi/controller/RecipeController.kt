package jmantello.secretrecipeapi.controller

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import jakarta.servlet.http.HttpServletRequest
import jmantello.secretrecipeapi.service.AuthenticationService
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.TokenService
import jmantello.secretrecipeapi.transfer.model.RecipeDTO
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.transfer.request.PublishRecipeRequest
import jmantello.secretrecipeapi.transfer.request.PublishReviewRequest
import jmantello.secretrecipeapi.transfer.request.UpdateRecipeRequest
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.ResponseBuilder.respond
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recipes")
class RecipeController(
    private val meterRegistry: MeterRegistry,
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
    fun createRecipe(@RequestBody request: PublishRecipeRequest): ResponseEntity<ApiResponse<RecipeDTO>> =
        respond(recipeService.create(request))

    @PutMapping("/{id}")
    fun updateRecipe(
        @PathVariable id: Long,
        @RequestBody request: UpdateRecipeRequest
    ): ResponseEntity<ApiResponse<RecipeDTO>> =
        respond(recipeService.update(id, request))

    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> =
        respond(recipeService.deleteById(id))

    @GetMapping("/{id}/reviews")
    fun getReviewsForRecipe(@PathVariable id: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(recipeService.getReviewsForRecipe(id))

    @PostMapping("/{id}/reviews")
    fun createReviewForRecipe(
        @PathVariable id: Long,
        @RequestBody request: PublishReviewRequest
    ): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(recipeService.createReviewForRecipe(id, request))

    @PostMapping("/{id}/save")
    fun saveRecipe(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<ApiResponse<List<RecipeDTO>>> {
        val user = when (val result = authenticationService.getCurrentAuthenticatedUser()) {
            is Success -> result.data
            is Error -> return respond(Error(result.message))
        }

        return respond(recipeService.saveRecipe(id, user))
    }

    @PostMapping("/{id}/unsave")
    fun unsaveRecipe(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<ApiResponse<List<RecipeDTO>>> {
        val user = when (val result = authenticationService.getCurrentAuthenticatedUser()) {
            is Success -> result.data
            is Error -> return respond(Error(result.message))
        }

        return respond(recipeService.unsaveRecipe(id, user))
    }
}