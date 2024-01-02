package jmantello.secretrecipeapi.transfer.model

class RecipeDTO(
    val id: Long,
    val title: String,
    val content: String,
    val publisherId: Long,
    val datePublished: String,
    val reviews: List<Long>,
    val tags: List<String>,
    val rating: Double?,
    var isPrivate: Boolean,
)