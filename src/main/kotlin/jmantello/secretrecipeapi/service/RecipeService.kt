package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.repository.RecipeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RecipeService(private val repository: RecipeRepository) {
    fun findAll(): Iterable<Recipe> = repository.findAll()
    fun findByIdOrNull(id: Long): Recipe? = repository.findByIdOrNull(id)
    fun findAllByUserId(userId: Long): MutableList<Recipe> = repository.findAllByUserId()
    fun save(recipe: Recipe): Recipe = repository.save(recipe)
    fun deleteById(id: Long): Unit = repository.deleteById(id)
}