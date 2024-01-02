package jmantello.secretrecipeapi.service

import jakarta.transaction.Transactional
import jmantello.secretrecipeapi.transfer.request.PublishReviewRequest
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.ReviewDTO
import jmantello.secretrecipeapi.entity.builder.ReviewBuilder
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.ReviewRepository
import jmantello.secretrecipeapi.repository.UserRepository
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.recipeNotFoundMessage
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.reviewNotFoundMessage
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.userNotFoundMessage
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.*
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
            ?: return Error(NOT_FOUND, reviewNotFoundMessage(id))

        return Success(review.toDTO())
    }

    fun findByIdOrNull(id: Long): Review? =
        reviewRepository.findByIdOrNull(id)

    fun findAllPublishedByUserId(publisherId: Long?): MutableList<Review> =
        reviewRepository.findAllByPublisherId(publisherId)

    fun save(review: Review): Review =
        reviewRepository.save(review)

    fun update(review: ReviewDTO): Result<ReviewDTO> {
        val id = review.id

        val review = reviewRepository.findByIdOrNull(id)
            ?: return Error(NOT_FOUND, reviewNotFoundMessage(id))

        val response = reviewRepository.save(review).toDTO()

        return Success(response)
    }

    fun create(request: PublishReviewRequest): Result<ReviewDTO> {
        val user = userRepository.findByIdOrNull(request.publisherId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(request.publisherId))

        val recipe = recipeRepository.findByIdOrNull(request.recipeId)
            ?: return Error(NOT_FOUND, recipeNotFoundMessage(request.recipeId))

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