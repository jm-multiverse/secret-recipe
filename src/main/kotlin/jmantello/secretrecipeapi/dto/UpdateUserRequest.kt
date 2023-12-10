package jmantello.secretrecipeapi.dto

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.User
import javax.validation.constraints.*

class UpdateUserRequest(
    @Email
    val email: String?,

    // TODO: Add custom validation
    val password: String?,

    @Size(min = 1, max = 100)
    val displayName: String?,

    val isAdmin: Boolean?,
    val publishedRecipes: List<Recipe>?,
    val savedRecipes: List<Recipe>?,
    val publishedReviews: List<Review>?,
    val followers: List<User>?,
    val following: List<User>?
)