package jmantello.secretrecipeapi.dto

import javax.validation.constraints.Size

class CreateRecipeDTO(
    val publisherId: Long,

    @Size(max = 100)
    val title: String,

    @Size(max = 1000) // Adjust to content limits
    val content: String,

    @Size(max = 10) // Assuming a maximum of 10 tags
    val tags: List<String>?,

    val isPrivate: Boolean?,
)