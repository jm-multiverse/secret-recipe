package jmantello.secretrecipeapi.controller

import jmantello.secretrecipeapi.transfer.request.PublishReviewRequest
import jmantello.secretrecipeapi.entity.ReviewDTO
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.ResponseBuilder.respond
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
    fun getReviews(): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(service.findAll())

    @GetMapping("/{id}")
    fun getReviewById(@PathVariable id: Long): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(service.findById(id))

    @PostMapping
    fun publishReview(@RequestBody request: PublishReviewRequest): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(service.create(request))

    // TODO: Create update review transfer
    @PutMapping
    fun updateReview(@RequestBody review: ReviewDTO): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(service.update(review))

    @DeleteMapping("/{id}")
    fun deleteReview(@PathVariable id: Long): ResponseEntity<Any> =
        ResponseEntity.ok(service.deleteById(id))

}