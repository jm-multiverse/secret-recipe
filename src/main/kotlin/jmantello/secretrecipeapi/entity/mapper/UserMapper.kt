package jmantello.secretrecipeapi.entity.mapper

import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.entity.builder.UserBuilder

object UserMapper {
    fun toDto(user: User): UserDTO {
        return UserDTO(
            id = user.id,
            email = user.email,
            displayName = user.displayName,
            isAdmin = user.isAdmin,
            isActive = user.isActive,
            dateCreated = user.dateCreated,
            publishedRecipes = user.publishedRecipes.map { it.toDTO() },
            savedRecipes = user.savedRecipes.map { it.toDTO() },
            publishedReviews = user.publishedReviews.map { it.toDTO() },
            followers = user.followers.map { it.toDTO() },
            following = user.following.map { it.toDTO() }
        )
    }
}