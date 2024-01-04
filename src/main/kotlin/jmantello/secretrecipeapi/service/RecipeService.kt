package jmantello.secretrecipeapi.service

import jakarta.transaction.Transactional
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.entity.builder.RecipeBuilder
import jmantello.secretrecipeapi.entity.builder.ReviewBuilder
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.ReviewRepository
import jmantello.secretrecipeapi.repository.UserRepository
import jmantello.secretrecipeapi.transfer.model.RecipeDTO
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.transfer.request.PublishRecipeRequest
import jmantello.secretrecipeapi.transfer.request.PublishReviewRequest
import jmantello.secretrecipeapi.transfer.request.UpdateRecipeRequest
import jmantello.secretrecipeapi.util.ErrorFactory.Companion.recipeNotFoundError
import jmantello.secretrecipeapi.util.ErrorFactory.Companion.userNotFoundError
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.stereotype.Service

@Service
@Transactional
class RecipeService(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository,
) {
    fun findAll(): Result<List<RecipeDTO>> =
        Success(recipeRepository.findAll().map { it.toDTO() })

    fun findById(id: Long): Result<RecipeDTO> {
        val recipe = recipeRepository.findByIdOrNull(id)
            ?: return recipeNotFoundError(id)

        return Success(recipe.toDTO())
    }

    fun create(request: PublishRecipeRequest): Result<RecipeDTO> {
        val publisherId = request.publisherId
        val user = userRepository.findByIdOrNull(publisherId)
            ?: return userNotFoundError(publisherId)

        val recipe = RecipeBuilder()
            .publisher(user)
            .title(request.title)
            .content(request.content)
            .tags(request.tags)
            .build()

        val response = recipeRepository.save(recipe).toDTO()
        return Success(CREATED, response)
    }

    fun update(id: Long, request: UpdateRecipeRequest): Result<RecipeDTO> {
        val recipe = recipeRepository.findByIdOrNull(id)
            ?: return recipeNotFoundError(id)

        recipe.update(request)
        val response = recipeRepository.save(recipe).toDTO()
        return Success(response)
    }

    fun deleteById(id: Long): Result<Unit> =
        Success(NO_CONTENT, recipeRepository.deleteById(id))

    fun getReviewsForRecipe(id: Long): Result<List<ReviewDTO>> {
        val recipe = recipeRepository.findByIdOrNull(id)
            ?: return recipeNotFoundError(id)

        return Success(recipe.reviews.map { it.toDTO() })
    }

    fun createReviewForRecipe(id: Long, request: PublishReviewRequest): Result<ReviewDTO> {
        val recipe = recipeRepository.findByIdOrNull(id)
            ?: return recipeNotFoundError(id)

        val publisherId = request.publisherId
        val user = userRepository.findByIdOrNull(publisherId)
            ?: return userNotFoundError(publisherId)

        val review = ReviewBuilder()
            .publisher(user)
            .recipe(recipe)
            .title(request.title)
            .content(request.content)
            .build()

        val response = reviewRepository.save(review).toDTO()
        return Success(CREATED, response)

    }

    fun saveRecipe(id: Long, user: User): Result<List<RecipeDTO>> {
        val recipe = recipeRepository.findByIdOrNull(id)
            ?: return recipeNotFoundError(id)

        user.savedRecipes.add(recipe)
        userRepository.save(user)

        val response = user.savedRecipes.map { it.toDTO() }

        return Success(response)
    }
}
