package jmantello.secretrecipeapi.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class UpdateRecipeDTO(
    @NotNull
    val id: Long? = null,

    val publisherId: Long? = null,

    @Size(max = 100)
    val title: String? = null,

    @Size(max = 1000) // Adjust to content limits
    val content: String? = null,

    val isPrivate: Boolean? = null,

    @Size(max = 10) // Assuming a maximum of 10 tags
    val tags: List<String>? = null,
)