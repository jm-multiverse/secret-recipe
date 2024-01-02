package jmantello.secretrecipeapi.controller

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import jmantello.secretrecipeapi.dto.PublishRecipeDTO
import jmantello.secretrecipeapi.dto.UpdateRecipeDTO
import jmantello.secretrecipeapi.entity.RecipeDTO
import jmantello.secretrecipeapi.entity.ReviewDTO
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.ResponseBuilder.respond
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recipes")
class RecipeController(
    private val meterRegistry: MeterRegistry,
    private val recipeService: RecipeService,
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
    fun createRecipe(@RequestBody request: PublishRecipeDTO): ResponseEntity<ApiResponse<RecipeDTO>> =
        respond(recipeService.create(request))

    @PutMapping("/{id}")
    fun updateRecipe(
        @PathVariable id: Long,
        @RequestBody updateRecipeDTO: UpdateRecipeDTO
    ): ResponseEntity<ApiResponse<RecipeDTO>> =
        respond(recipeService.update(id, updateRecipeDTO))

    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> =
        respond(recipeService.deleteById(id))

    @GetMapping("/{id}/reviews")
    fun getReviewsForRecipe(@PathVariable id: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(recipeService.getReviewsForRecipe(id))
}