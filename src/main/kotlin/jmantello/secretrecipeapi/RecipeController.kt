package jmantello.secretrecipeapi

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class RecipeController(private val rs: RecipeService) {

    @GetMapping("/")
    fun index(): String = "Welcome"

    @GetMapping("/recipe")
    fun getRecipes(): Collection<Recipe> = rs.getRecipes()

    @GetMapping("/recipe/{id}")
    fun getRecipeById(@PathVariable id: Int): Recipe = rs.getRecipeById(id)

    @PostMapping("/recipe")
    fun createRecipe(@RequestBody recipe: Recipe) = rs.createRecipe(recipe)

    @PutMapping("/recipe/{id}")
    fun updateRecipe(@RequestBody recipe: Recipe) = rs.updateRecipe(recipe)

    @DeleteMapping("/recipe/{id}")
    fun deleteRecipe(@PathVariable id: Int) = rs.deleteRecipe(id)

}