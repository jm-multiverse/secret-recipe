package jmantello.secretrecipeapi.transfer.model

import jmantello.secretrecipeapi.entity.User

class UserDTO(
    val id: Long,
    val email: String,
    val displayName: String,
    val dateCreated: String,
    val publishedRecipes: List<Long>,
    val savedRecipes: List<Long>,
    val publishedReviews: List<Long>,
    val followers: List<Long>,
    val following: List<Long>,
    val roles: List<User.Role>,
    val status: User.Status,
)