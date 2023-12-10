package jmantello.secretrecipeapi.entity.builder

import jmantello.secretrecipeapi.dto.RegisterUserRequest
import jmantello.secretrecipeapi.dto.UpdateUserRequest
import jmantello.secretrecipeapi.entity.User

class UserBuilder {
    private val user = User()

    fun email(email: String): UserBuilder {
        user.email = email
        return this
    }

    fun password(password: String): UserBuilder {
        user.password = password
        return this
    }

    fun displayName(displayName: String): UserBuilder {
        user.displayName = displayName
        return this
    }

    fun isAdmin(isAdmin: Boolean): UserBuilder {
        user.isAdmin = isAdmin
        return this
    }

    fun isActive(isActive: Boolean): UserBuilder {
        user.isActive = isActive
        return this
    }

    fun buildFromRegisterRequest(request: RegisterUserRequest): User {
        return User().apply {
            email = request.email
            password = request.password
            displayName = request.displayName
        }
    }

    fun buildFromDTO(userDTO: UpdateUserRequest, user: User): User {
        return user.apply {
            userDTO.email?.let { this.email = it }
            userDTO.password?.let { this.password = it }
            userDTO.displayName?.let { this.displayName = it }
            userDTO.isAdmin?.let { this.isAdmin = it }
            userDTO.publishedRecipes?.let { this.publishedRecipes = it.toMutableList() }
            userDTO.savedRecipes?.let { this.savedRecipes = it.toMutableList() }
            userDTO.publishedReviews?.let { this.publishedReviews = it.toMutableList() }
            userDTO.followers?.let { this.followers = it.toMutableList() }
            userDTO.following?.let { this.following = it.toMutableList() }
        }
    }

    fun build(): User {
        return user
    }
}