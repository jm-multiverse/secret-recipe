package jmantello.secretrecipeapi

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
    fun getRecipes(): Iterable<Recipe> = service.getRecipes()

    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Long): Recipe? = service.getRecipeById(id)

    @PostMapping
    fun createRecipe(@RequestBody recipe: Recipe): Recipe = service.createRecipe(recipe)

    @PutMapping("/{id}")
    fun updateRecipe(@RequestBody recipe: Recipe): Recipe = service.updateRecipe(recipe)

    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: Long) = service.deleteRecipe(id)

}