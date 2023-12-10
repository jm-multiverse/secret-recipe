package jmantello.secretrecipeapi.controller

import jmantello.secretrecipeapi.dto.CreateReviewRequest
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.service.ReviewService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reviews")
class ReviewController(private val service: ReviewService) {
    @GetMapping
    fun getReviews(): ResponseEntity<Any> =
        ResponseEntity.ok(service.findAll())

    @GetMapping("/{id}")
    fun getReviewById(@PathVariable id: Long): ResponseEntity<Any> {
        val review = service.findByIdOrNull(id)
            ?: return ResponseEntity.status(404).body("Review with id $id not found.")

        return ResponseEntity.ok(review)
    }

    @PostMapping
    fun publishReview(@RequestBody request: CreateReviewRequest): ResponseEntity<Any> =
        ResponseEntity.status(201).body(service.publish(request))

    @PutMapping
    fun updateReview(@RequestBody review: Review): ResponseEntity<Any> =
        ResponseEntity.ok(service.save(review))

    @DeleteMapping("/{id}")
    fun deleteReview(@PathVariable id: Long): ResponseEntity<Any> =
        ResponseEntity.ok(service.deleteById(id))

}