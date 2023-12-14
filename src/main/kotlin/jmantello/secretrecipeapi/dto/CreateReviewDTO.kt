package jmantello.secretrecipeapi.dto

class CreateReviewDTO(
    val publisherId: Long,
    val title: String,
    val content: String,
    val rating: Double,
)