package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.repository.ReviewRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ReviewService(private val repository: ReviewRepository) {
    fun getReviews(): Iterable<Review> = repository.findAll()
    fun getReviewById(id: Long): Review? = repository.findByIdOrNull(id)
    fun createReview(review: Review): Review = repository.save(review)
    fun updateReview(review: Review): Review = repository.save(review)
    fun deleteReview(id: Long): Unit = repository.deleteById(id)
}