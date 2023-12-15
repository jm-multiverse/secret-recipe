package jmantello.secretrecipeapi.dto

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

class SaveReviewDTO(
    val publisherId: Long,

    @Size(max = 100) // Title length limit
    val title: String,

    @Min(1)
    @Max(5) // Assuming a rating scale of 1 to 5
    val rating: Double,

    @Size(max = 500) // Content length limit
    val content: String?,

    val isPrivate: Boolean?
)