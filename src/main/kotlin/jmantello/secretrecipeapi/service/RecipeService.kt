package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.dto.CreateRecipeDTO
import jmantello.secretrecipeapi.dto.UpdateRecipeDTO
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
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository,
) {
    fun findAll(): Result<List<RecipeDTO>> =
        Success(recipeRepository.findAll().map { it.toDTO() })

    fun findById(id: Long): Result<RecipeDTO>  {
        val recipe = recipeRepository.findByIdOrNull(id)
            ?: return Error(NOT_FOUND, recipeNotFoundMessage(id))

        return Success(recipe.toDTO())
    }

    fun findByIdOrNull(id: Long): Recipe? =
        recipeRepository.findByIdOrNull(id)

    fun create(request: CreateRecipeDTO): Result<RecipeDTO> {
        val publisherId = request.publisherId
        val user = userRepository.findByIdOrNull(publisherId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(publisherId))

        val recipe = RecipeBuilder()
            .publisher(user)
            .title(request.title)
            .content(request.content)
            .tags(request.tags)
            .build()

        val response = recipeRepository.save(recipe).toDTO()
        return Success(CREATED, response)
    }

    fun update(id:Long, request: UpdateRecipeDTO): Result<RecipeDTO> {
        val recipe = recipeRepository.findByIdOrNull(id)
            ?: return Error(NOT_FOUND, recipeNotFoundMessage(id))

        recipe.update(request)
        val response = recipeRepository.save(recipe).toDTO()
        return Success(response)
    }

    fun deleteById(id: Long): Result<Unit> =
        Success(NO_CONTENT, recipeRepository.deleteById(id))
}
