package jmantello.secretrecipeapi

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/api/recipe")
class RecipeController(private val rs: RecipeService) {

    @GetMapping("/")
    fun getRecipes(): Collection<Recipe> = rs.getRecipes()

    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Int): Recipe = rs.getRecipeById(id)

    @PostMapping("/")
    fun createRecipe(@RequestBody recipe: Recipe) = rs.createRecipe(recipe)

    @PutMapping("/{id}")
    fun updateRecipe(@RequestBody recipe: Recipe) = rs.updateRecipe(recipe)

    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: Int) = rs.deleteRecipe(id)

}