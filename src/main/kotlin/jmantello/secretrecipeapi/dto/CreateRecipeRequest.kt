package jmantello.secretrecipeapi.dto

class CreateRecipeRequest(
    val publisherId: Long,
    val title: String,
    val content: String,
)