package jmantello.secretrecipeapi.dto

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.User
import javax.validation.constraints.*

class UpdateReviewDTO(
    @Size(max = 100) // Title length limit
    val title: String?,

    @Min(1)
    @Max(5) // Assuming a rating scale of 1 to 5
    val rating: Double?,

    @Size(max = 500) // Content length limit
    val content: String?,

    val isPrivate: Boolean?
)