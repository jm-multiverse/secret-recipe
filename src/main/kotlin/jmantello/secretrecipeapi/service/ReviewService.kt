package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.repository.ReviewRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ReviewService(private val repository: ReviewRepository) {
    fun findAll(): Iterable<Review> = repository.findAll()
    fun findByIdOrNull(id: Long): Review? = repository.findByIdOrNull(id)
    fun save(review: Review): Review = repository.save(review)
    fun deleteById(id: Long): Unit = repository.deleteById(id)
}