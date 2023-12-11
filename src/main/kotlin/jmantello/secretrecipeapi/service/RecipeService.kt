package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.dto.CreateRecipeRequest
import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.RecipeDTO
import jmantello.secretrecipeapi.entity.builder.RecipeBuilder
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.UserRepository
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.recipeNotFoundMessage
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.userNotFoundMessage
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.stereotype.Service

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository,
) {
    fun findAll(): Result<List<RecipeDTO>> =
        Success(recipeRepository.findAll().map { it.toDTO() })

    fun findById(id: Long): Result<Recipe>  {
        val recipe = recipeRepository.findByIdOrNull(id)
            ?: return Error(NOT_FOUND, recipeNotFoundMessage(id))

        return Success(recipe)
    }

    fun findByIdOrNull(id: Long): Recipe? =
        recipeRepository.findByIdOrNull(id)

    fun create(request: CreateRecipeRequest): Result<Recipe> {
        val publisherId = request.publisherId
        val user = userRepository.findByIdOrNull(publisherId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(publisherId))


        val recipe = RecipeBuilder()
            .publisher(user)
            .title(request.title)
            .content(request.content)
            .build()

        val response = recipeRepository.save(recipe)
        return Success(response)
    }

    fun update(recipe: Recipe): Result<Recipe> =
        Success(recipeRepository.save(recipe))

    fun deleteById(id: Long): Result<Unit> =
        Success(NO_CONTENT, recipeRepository.deleteById(id))
}
