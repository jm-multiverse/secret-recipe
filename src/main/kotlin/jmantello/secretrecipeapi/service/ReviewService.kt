package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.dto.CreateReviewRequest
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.ReviewDTO
import jmantello.secretrecipeapi.entity.builder.ReviewBuilder
import jmantello.secretrecipeapi.repository.ReviewRepository
import jmantello.secretrecipeapi.repository.UserRepository
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.notFoundMessage
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.reviewNotFoundMessage
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.userNotFoundMessage
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) {
    fun findAll(): Result<List<ReviewDTO>> =
        Success(reviewRepository.findAll().map { it.toDTO() })

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

    fun create(request: CreateReviewRequest): Result<ReviewDTO> {
        val user = userRepository.findByIdOrNull(request.publisherId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(request.publisherId))

        val review = ReviewBuilder()
            .publisher(user)
            .title(request.title)
            .rating(request.rating)
            .content(request.content)
            .build()

        val response = reviewRepository.save(review).toDTO()
        return Success(CREATED, response)
    }

    fun deleteById(id: Long): Unit =
        reviewRepository.deleteById(id)
}