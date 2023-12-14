package jmantello.secretrecipeapi.controller

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import jmantello.secretrecipeapi.dto.CreateRecipeDTO
import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.RecipeDTO
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.ResponseBuilder.respond
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recipes")
class RecipeController(private val service: RecipeService, private val meterRegistry: MeterRegistry) {
    // Define custom metrics
    val requestsCounter: Counter = Counter.builder("requests.count")
        .tag("controller", "recipe")
        .register(meterRegistry)

    val processingTime: Timer = Timer.builder("requests.processing.time")
        .tag("controller", "recipe")
        .register(meterRegistry)

    @GetMapping
    fun getRecipes(): ResponseEntity<ApiResponse<List<RecipeDTO>>> =
        respond(service.findAll())

    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Long): ResponseEntity<ApiResponse<Recipe>> =
        respond(service.findById(id))

    @PostMapping
    fun createRecipe(@RequestBody recipeRequest: CreateRecipeDTO): ResponseEntity<ApiResponse<Recipe>> =
        respond(service.create(recipeRequest))

    // TODO: Create UpdateRecipe DTO
    @PutMapping
    fun updateRecipe(@RequestBody recipe: Recipe): ResponseEntity<ApiResponse<Recipe>> =
        respond(service.update(recipe))

    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> =
        respond(service.deleteById(id))

}