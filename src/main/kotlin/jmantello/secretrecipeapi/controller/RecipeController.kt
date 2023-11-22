package jmantello.secretrecipeapi.controller

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import jmantello.secretrecipeapi.entity.RecipeRequest
import jmantello.secretrecipeapi.resourceCreated
import jmantello.secretrecipeapi.resourceNotFound
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

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
    fun getRecipes(): ResponseEntity<Any> =
        ResponseEntity.ok(service.findAll())

    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Long): ResponseEntity<Any> {
        val recipe = service.findByIdOrNull(id)
            ?: return resourceNotFound("Recipe with id $id not found.")

        return ResponseEntity.ok(recipe)
    }

    @PostMapping
    @PutMapping
    fun saveRecipe(@RequestBody recipeRequest: RecipeRequest): ResponseEntity<Any> {
        return when(val result = service.save(recipeRequest)) {
            is Result.Success -> resourceCreated(result)
            is Result.Error -> ResponseEntity.badRequest().body(result.message)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<Any> =
        ResponseEntity.ok(service.deleteById(id))

}