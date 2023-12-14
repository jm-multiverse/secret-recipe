package jmantello.secretrecipeapi.dto

class CreateRecipeDTO(
    val publisherId: Long,
    val title: String,
    val content: String,
)