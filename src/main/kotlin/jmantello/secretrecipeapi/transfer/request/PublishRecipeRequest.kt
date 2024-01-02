package jmantello.secretrecipeapi.transfer.request

import javax.validation.constraints.Size

class PublishRecipeRequest(
    val publisherId: Long,

    @Size(max = 100)
    val title: String,

    @Size(max = 1000) // Adjust to content limits
    val content: String,

    @Size(max = 10) // Assuming a maximum of 10 tags
    val tags: List<String> = listOf(),

    val isPrivate: Boolean? = false,
)