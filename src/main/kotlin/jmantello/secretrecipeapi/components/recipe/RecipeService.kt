package jmantello.secretrecipeapi

import jmantello.secretrecipeapi.components.recipe.RecipeModel
import jmantello.secretrecipeapi.components.recipe.RecipeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RecipeService(private val repository: RecipeRepository) {
    fun get(): Iterable<RecipeModel> = repository.findAll()
    fun getById(id: Long): RecipeModel? = repository.findByIdOrNull(id)
    fun create(recipe: RecipeModel): RecipeModel = repository.save(recipe)
    fun update(recipe: RecipeModel): RecipeModel = repository.save(recipe)
    fun delete(id: Long): Unit = repository.deleteById(id)
}