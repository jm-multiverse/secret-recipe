package jmantello.secretrecipeapi

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/recipe")
class RecipeController(private val service: RecipeService) {

    @GetMapping
    fun getRecipes(): ResponseEntity<Any> =
        ResponseEntity.ok(service.getRecipes())

    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Long): ResponseEntity<Any> {
        val recipe = service.getRecipeById(id)
            ?: return ResponseEntity.status(404).body("Recipe with id $id not found.")

        return ResponseEntity.ok(recipe)
    }

    @PostMapping
    fun createRecipe(@RequestBody recipe: Recipe): ResponseEntity<Any> =
        ResponseEntity.status(201).body(service.createRecipe(recipe))

    @PutMapping
    fun updateRecipe(@RequestBody recipe: Recipe): ResponseEntity<Any> =
        ResponseEntity.ok(service.updateRecipe(recipe))

    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<Any> =
        ResponseEntity.ok(service.deleteRecipe(id))

}