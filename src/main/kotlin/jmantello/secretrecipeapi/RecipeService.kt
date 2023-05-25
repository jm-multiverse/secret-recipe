package jmantello.secretrecipeapi

import org.springframework.data.repository.findByIdOrNull

class RecipeService(private val repository: RecipeRepository) {
    fun getRecipes(): Iterable<Recipe> = repository.findAll()
    fun getRecipeById(id: Long): Recipe? = repository.findByIdOrNull(id)
    fun createRecipe(recipe: Recipe): Recipe = repository.save(recipe)
    fun updateRecipe(recipe: Recipe): Recipe = repository.save(recipe)
    fun deleteRecipe(id: Long): Unit = repository.deleteById(id)
}