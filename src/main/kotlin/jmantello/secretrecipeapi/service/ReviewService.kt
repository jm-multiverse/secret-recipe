package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.PublishReviewRequest
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.repository.ReviewRepository
import jmantello.secretrecipeapi.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) {
    fun findAll(): Iterable<Review> = reviewRepository.findAll()
    fun findByIdOrNull(id: Long): Review? = reviewRepository.findByIdOrNull(id)
    fun findAllPublishedByUserId(publisherId: Long?): MutableList<Review> = reviewRepository.findAllByPublisherId(publisherId)
    fun save(review: Review): Review = reviewRepository.save(review)
    fun publish(reviewRequest: PublishReviewRequest): Result<Review> {
        val user = userRepository.findByIdOrNull(reviewRequest.publisherId)
            ?: return Result.Error("User with id ${reviewRequest.publisherId} not found")

        val review = Review()
        review.publisher = user
        review.title = reviewRequest.title
        review.rating = reviewRequest.rating
        review.content = reviewRequest.content

        val response = reviewRepository.save(review)
        return Result.Success(response)
    }
    fun deleteById(id: Long): Unit = reviewRepository.deleteById(id)
}