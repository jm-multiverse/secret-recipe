package jmantello.secretrecipeapi.transfer

import javax.validation.constraints.Size

class PublishReviewRequest(

    val publisherId: Long,
    val recipeId: Long,

    @Size(max = 100)
    val title: String,

    @Size(max = 1000)
    val content: String,

    val rating: Double,

    val isPrivate: Boolean = false,
)