package jmantello.secretrecipeapi.transfer.request

import javax.validation.constraints.Size

class PublishReviewRequest(

    @Size(max = 100)
    val title: String,

    @Size(max = 1000)
    val content: String,

    val rating: Double,

    val isPrivate: Boolean = false,
)