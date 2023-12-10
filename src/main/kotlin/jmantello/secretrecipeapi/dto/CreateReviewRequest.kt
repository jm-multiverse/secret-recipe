package jmantello.secretrecipeapi.dto

class CreateReviewRequest(
    val publisherId: Long,
    val title: String,
    val content: String,
    val rating: Double,
)