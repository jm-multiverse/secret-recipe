package jmantello.secretrecipeapi.service

import jakarta.transaction.Transactional
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.entity.builder.ReviewBuilder
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.ReviewRepository
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.transfer.request.PublishReviewRequest
import jmantello.secretrecipeapi.transfer.request.UpdateReviewRequest
import jmantello.secretrecipeapi.util.ErrorResponses.Companion.recipeNotFoundError
import jmantello.secretrecipeapi.util.ErrorResponses.Companion.reviewNotFoundError
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.CREATED
import org.springframework.stereotype.Service

@Service
class ReviewService(
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

    fun update(reviewId: Long, request: UpdateReviewRequest): Result<ReviewDTO> {
        val review = reviewRepository.findByIdOrNull(reviewId)
            ?: return reviewNotFoundError(reviewId)

        review.update(request)
        val response = reviewRepository.save(review).toDTO()
        return Success(response)
    }

    fun deleteById(id: Long): Unit =
        reviewRepository.deleteById(id)
}