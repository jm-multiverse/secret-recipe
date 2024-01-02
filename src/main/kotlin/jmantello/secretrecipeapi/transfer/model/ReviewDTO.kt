package jmantello.secretrecipeapi.transfer.model

class ReviewDTO(
    val id: Long,
    val title: String,
    val content: String?,
    val publisherId: Long,
    val recipeId: Long,
    val datePublished: String,
    val likes: List<Long>?,
    val rating: Double?,
    val isPrivate: Boolean
)