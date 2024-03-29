package jmantello.secretrecipeapi.entity.mapper

import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.transfer.model.UserDTO

object UserMapper {
    fun toDto(user: User): UserDTO {
        return UserDTO(
            id = user.id,
            email = user.email,
            displayName = user.displayName,
            dateCreated = user.dateCreated,
            publishedRecipes = user.publishedRecipes.map { it.id },
            savedRecipes = user.savedRecipes.map { it.id },
            publishedReviews = user.publishedReviews.map { it.id },
            followers = user.followers.map { it.id },
            following = user.following.map { it.id },
            roles = user.roles,
            status = user.status
        )
    }
}