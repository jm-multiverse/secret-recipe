package jmantello.secretrecipeapi.entity.builder

import jmantello.secretrecipeapi.dto.RegisterUserDTO
import jmantello.secretrecipeapi.dto.UpdateUserDTO
import jmantello.secretrecipeapi.entity.User
import java.time.LocalDateTime

class UserBuilder {
    private var email: String = ""
    private var password: String = ""
    private var displayName: String? = null
    private var isAdmin: Boolean = false
    private var isActive: Boolean = true
    private var dateCreated: String? = null
    private var publishedRecipes: MutableList<Long> = mutableListOf()
    private var savedRecipes: MutableList<Long> = mutableListOf()
    private var publishedReviews: MutableList<Long> = mutableListOf()
    private var followers: MutableList<Long> = mutableListOf()
    private var following: MutableList<Long> = mutableListOf()

    fun email(email: String): UserBuilder {
        this.email = email
        return this
    }

    fun password(password: String): UserBuilder {
        this.password = password
        return this
    }

    fun displayName(displayName: String): UserBuilder {
        this.displayName = displayName
        return this
    }

    fun isAdmin(isAdmin: Boolean): UserBuilder {
        this.isAdmin = isAdmin
        return this
    }

    fun isActive(isActive: Boolean): UserBuilder {
        this.isActive = isActive
        return this
    }

    fun dateCreated(dateCreated: String): UserBuilder {
        this.dateCreated = dateCreated
        return this
    }

    fun publishedRecipes(publishedRecipes: MutableList<Long>): UserBuilder {
        this.publishedRecipes = publishedRecipes
        return this
    }

    fun savedRecipes(savedRecipes: MutableList<Long>): UserBuilder {
        this.savedRecipes = savedRecipes
        return this
    }

    fun publishedReviews(publishedReviews: MutableList<Long>): UserBuilder {
        this.publishedReviews = publishedReviews
        return this
    }

    fun followers(followers: MutableList<Long>): UserBuilder {
        this.followers = followers
        return this
    }

    fun following(following: MutableList<Long>): UserBuilder {
        this.following = following
        return this
    }

    fun buildFromRegisterRequest(request: RegisterUserDTO): User {
        return User(
            email = request.email,
            password = request.password,
            displayName = request.displayName,
            isAdmin = request.isAdmin,
            isActive = true,
            dateCreated = LocalDateTime.now().toString(),
            publishedRecipes = mutableListOf(),
            savedRecipes = mutableListOf(),
            publishedReviews = mutableListOf(),
            followers = mutableListOf(),
            following = mutableListOf()
        )
    }

    fun build(): User {
        return User(
            email = email,
            password = password,
            displayName = displayName,
            isAdmin = isAdmin,
            isActive = isActive,
            dateCreated = dateCreated ?: LocalDateTime.now().toString(),
            publishedRecipes = mutableListOf(),
            savedRecipes = mutableListOf(),
            publishedReviews = mutableListOf(),
            followers = mutableListOf(),
            following = mutableListOf()
        )
    }
}