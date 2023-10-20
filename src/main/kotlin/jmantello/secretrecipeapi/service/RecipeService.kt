package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.exception.ResourceNotFoundException
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

class RecipeNotFoundException(recipeId: Long) : ResourceNotFoundException("Recipe with ID $recipeId not found")

@Service
class RecipeService(
    private val userRepository: UserRepository,
    private val repository: RecipeRepository
) {
    fun findAll(): Iterable<Recipe> = repository.findAll()
    fun findById(id: Long): Optional<Recipe> = repository.findById(id)
    fun findByIdOrNull(id: Long): Recipe? = repository.findByIdOrNull(id)
    fun findAllPublishedByUserId(publisherId: Long?): MutableList<Recipe> = repository.findAllByPublisherId(publisherId)
    fun save(recipe: Recipe): Recipe = repository.save(recipe)
    fun deleteById(id: Long): Unit = repository.deleteById(id)

    fun findAllSavedByUserId(userId: Long): List<Recipe>? {
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException(userId) }
        return user.savedRecipes
    }
}
