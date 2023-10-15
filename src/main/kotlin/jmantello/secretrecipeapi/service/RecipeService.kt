package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.repository.RecipeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RecipeService(private val repository: RecipeRepository) {
    fun getRecipes(): Iterable<Recipe> = repository.findAll()
    fun getRecipeById(id: Long): Recipe? = repository.findByIdOrNull(id)
    fun createRecipe(recipe: Recipe): Recipe = repository.save(recipe)
    fun updateRecipe(recipe: Recipe): Recipe = repository.save(recipe)
    fun deleteRecipe(id: Long): Unit = repository.deleteById(id)
}