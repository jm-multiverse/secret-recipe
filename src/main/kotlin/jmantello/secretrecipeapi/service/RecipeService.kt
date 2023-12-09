package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.dto.CreateRecipeRequest
import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.RecipeDTO
import jmantello.secretrecipeapi.entity.mapper.RecipeMapper
import jmantello.secretrecipeapi.exception.ResourceNotFoundException
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.UserRepository
import jmantello.secretrecipeapi.util.Result
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

class RecipeNotFoundException(recipeId: Long) : ResourceNotFoundException("Recipe with ID $recipeId not found")

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository,
) {
    fun findAll(): Iterable<Recipe> = recipeRepository.findAll()
    fun findByIdOrNull(id: Long): Recipe? = recipeRepository.findByIdOrNull(id)
    fun findAllPublishedByUserId(publisherId: Long?): MutableList<Recipe> = recipeRepository.findAllByPublisherId(publisherId)
    fun findAllSavedByUserId(userId: Long): List<RecipeDTO> {
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException(userId) }
        return user.savedRecipes.map { RecipeMapper.toDto(it) }
    }
    fun create(request: CreateRecipeRequest): Result<Recipe> {
        val user = userRepository.findByIdOrNull(request.publisherId)
            ?: return Result.Error("User with id ${request.publisherId} not found")

        val recipe = Recipe()
        recipe.publisher = user
        recipe.title = request.title
        recipe.content = request.content

        val response = recipeRepository.save(recipe)
        return Result.Success(response)
    }

    fun update(recipe: Recipe): Result<Recipe> = Result.Success(recipeRepository.save(recipe))

    fun deleteById(id: Long): Unit = recipeRepository.deleteById(id)
}
