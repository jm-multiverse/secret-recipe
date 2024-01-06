package jmantello.secretrecipeapi.service

import jakarta.transaction.Transactional
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.builder.ReviewBuilder
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.ReviewRepository
import jmantello.secretrecipeapi.repository.UserRepository
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.transfer.request.PublishReviewRequest
import jmantello.secretrecipeapi.util.ErrorFactory.Companion.recipeNotFoundError
import jmantello.secretrecipeapi.util.ErrorFactory.Companion.reviewNotFoundError
import jmantello.secretrecipeapi.util.ErrorFactory.Companion.userNotFoundError
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.CREATED
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository,
) {
    fun findAll(): Result<List<ReviewDTO>> =
        Success(reviewRepository.findAll().map { it.toDTO() })

    @Transactional
    fun findById(id: Long): Result<ReviewDTO> {
        val review = reviewRepository.findByIdOrNull(id)
            ?: return reviewNotFoundError(id)

        return Success(review.toDTO())
    }

    fun findByIdOrNull(id: Long): Review? =
        reviewRepository.findByIdOrNull(id)

    fun findAllPublishedByUserId(publisherId: Long?): MutableList<Review> =
        reviewRepository.findAllByPublisherId(publisherId)

    fun save(review: Review): Review =
        reviewRepository.save(review)

    fun update(request: ReviewDTO): Result<ReviewDTO> {
        val id = request.id
        val review = reviewRepository.findByIdOrNull(id)
            ?: return reviewNotFoundError(id)

        val response = reviewRepository.save(review).toDTO()

        return Success(response)
    }

    fun create(request: PublishReviewRequest): Result<ReviewDTO> {
        val user = userRepository.findByIdOrNull(request.publisherId)
            ?: return userNotFoundError(request.publisherId)

        val recipe = recipeRepository.findByIdOrNull(request.recipeId)
            ?: return recipeNotFoundError(request.recipeId)

        val review = ReviewBuilder()
            .publisher(user)
            .recipe(recipe)
            .title(request.title)
            .rating(request.rating)
            .content(request.content)
            .isPrivate(request.isPrivate)
            .build()

        val response = reviewRepository.save(review).toDTO()
        return Success(CREATED, response)
    }

    fun deleteById(id: Long): Unit =
        reviewRepository.deleteById(id)
}