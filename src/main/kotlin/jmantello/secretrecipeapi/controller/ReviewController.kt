package jmantello.secretrecipeapi.controller

import jmantello.secretrecipeapi.service.AuthenticationService
import jmantello.secretrecipeapi.transfer.request.PublishReviewRequest
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.UserService
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
    private val authenticationService: AuthenticationService,
) {
    @GetMapping
    fun getReviews(): ResponseEntity<ApiResponse<List<ReviewDTO>>> =
        respond(reviewService.findAll())

    @GetMapping("/{id}")
    fun getReviewById(@PathVariable id: Long): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(reviewService.findById(id))

    @PostMapping
    fun publishReview(@RequestBody request: PublishReviewRequest): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(reviewService.create(request))

    // TODO: Create updateReviewRequest
    @PutMapping
    fun updateReview(@RequestBody review: ReviewDTO): ResponseEntity<ApiResponse<ReviewDTO>> =
        respond(reviewService.update(review))

    @DeleteMapping("/{id}")
    fun deleteReview(@PathVariable id: Long): ResponseEntity<Any> =
        ResponseEntity.ok(reviewService.deleteById(id))

    @PostMapping("/{id}/like")
    fun likeReview(@PathVariable recipeId: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> {
        val currentUser = when (val result = authenticationService.getCurrentAuthenticatedUser()) {
            is Success -> result.data
            is Error -> return respond(unauthorizedError)
        }
        val result = userService.likeReview(currentUser.id, recipeId)
        return respond(result)
    }

    @PostMapping("/{id}/unlike")
    fun unlikeReview(@PathVariable recipeId: Long): ResponseEntity<ApiResponse<List<ReviewDTO>>> {
        val currentUser = when (val result = authenticationService.getCurrentAuthenticatedUser()) {
            is Success -> result.data
            is Error -> return respond(unauthorizedError)
        }
        val result = userService.unlikeReview(currentUser.id, recipeId)
        return respond(result)
    }

}