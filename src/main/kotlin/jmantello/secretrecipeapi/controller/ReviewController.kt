package jmantello.secretrecipeapi.controller

import jmantello.secretrecipeapi.service.AuthenticationService
import jmantello.secretrecipeapi.transfer.request.PublishReviewRequest
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.transfer.request.UpdateReviewRequest
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.ErrorResponses.Companion.unauthorizedError
import jmantello.secretrecipeapi.util.ResponseBuilder.respond
import jmantello.secretrecipeapi.util.Result.*
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
class ReviewController(
    private val reviewService: ReviewService,
    private val userService: UserService,
) {
    @GetMapping
    fun getReviews(): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(reviewService.findAll())

    @GetMapping("/{id}")
    fun getReviewById(@PathVariable id: Long): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(reviewService.findById(id))

    @PostMapping
    fun publishReview(@RequestBody request: PublishReviewRequest): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(Error("New reviews must be published through the recipe endpoint: 'api/recipes/{id}/publish'."))

    @PutMapping("/{id}")
    fun updateReview(@PathVariable id: Long, @RequestBody request: UpdateReviewRequest): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(reviewService.update(id, request))

    @DeleteMapping("/{id}")
    fun deleteReview(@PathVariable id: Long): ResponseEntity<Any> =
        ResponseEntity.ok(reviewService.deleteById(id))

    @PostMapping("/{reviewId}/like")
    fun likeReview(@PathVariable reviewId: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(userService.likeReview(reviewId))


    @PostMapping("/{reviewId}/unlike")
    fun unlikeReview(@PathVariable reviewId: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(userService.unlikeReview(reviewId))
}