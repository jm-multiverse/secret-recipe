package jmantello.secretrecipeapi.controller

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import jmantello.secretrecipeapi.ResponseEntity.Companion.badRequest
import jmantello.secretrecipeapi.ResponseEntity.Companion.created
import jmantello.secretrecipeapi.ResponseEntity.Companion.notFound
import jmantello.secretrecipeapi.entity.CreateRecipeRequest
import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.Result
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    fun getRecipes(): ResponseEntity<out Any> = ok(service.findAll())

    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Long): ResponseEntity<out Any> {
        val recipe = service.findByIdOrNull(id)
            ?: return notFound("Recipe with id $id not found.")

        return ok(recipe)
    }

    @PostMapping
    fun createRecipe(@RequestBody recipeRequest: CreateRecipeRequest): ResponseEntity<out Any> {
        return when(val result = service.create(recipeRequest)) {
            is Result.Success -> created(result)
            is Result.Error -> badRequest(result.message)
        }
    }

    @PutMapping
    fun updateRecipe(@RequestBody recipe: Recipe): ResponseEntity<out Any> {
        return when(val result = service.update(recipe)) {
            is Result.Success -> ok(result)
            is Result.Error -> badRequest(result.message)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<out Any> = ok(service.deleteById(id))

}