package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.CreateRecipeRequest
import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.RecipeResponse
import jmantello.secretrecipeapi.exception.ResourceNotFoundException
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.UserRepository
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
    fun findAllSavedByUserId(userId: Long): List<RecipeResponse> {
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException(userId) }
        return user.savedRecipes.map { recipeToResponse(it) }
    }
    fun create(recipeRequest: CreateRecipeRequest): Result<Recipe> {
        val user = userRepository.findByIdOrNull(recipeRequest.publisherId)
            ?: return Result.Error("User with id ${recipeRequest.publisherId} not found")

        val recipe = Recipe()
        recipe.publisher = user
        recipe.title = recipeRequest.title
        recipe.content = recipeRequest.content

        val response = recipeRepository.save(recipe)
        return Result.Success(response)
    }

    fun update(recipe: Recipe): Result<Recipe> = Result.Success(recipeRepository.save(recipe))

    fun deleteById(id: Long): Unit = recipeRepository.deleteById(id)

    private fun recipeToResponse(recipe: Recipe): RecipeResponse {
        return RecipeResponse(
            id = recipe.id,
            title = recipe.title,
            content = recipe.content,
            datePublished = recipe.datePublished,
            publisherId = recipe.publisher!!.id,
            tags = recipe.tags,
            reviews = recipe.reviews,
            isPrivate = recipe.isPrivate,
        )
    }
}
