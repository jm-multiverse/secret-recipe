package jmantello.secretrecipeapi.dto

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.User
import javax.validation.constraints.*

class UpdateRecipeDTO(
    @Size(max = 100)
    val title: String?,

    @Size(max = 1000) // Adjust to content limits
    val content: String?,

    val isPrivate: Boolean?,

    @Size(max = 10) // Assuming a maximum of 10 tags
    val tags: List<String>?
)