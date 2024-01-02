package jmantello.secretrecipeapi.entity.builder

import jmantello.secretrecipeapi.transfer.RegisterUserRequest
import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.Role
import jmantello.secretrecipeapi.entity.User
import java.time.LocalDateTime

class UserBuilder {
    private var email: String? = null
    private var password: String? = null
    private var displayName: String? = null
    private var isAdmin: Boolean? = null
    private var isActive: Boolean? = null
    private var dateCreated: String? = null
    private var publishedRecipes: MutableList<Recipe>? = null
    private var savedRecipes: MutableList<Recipe>? = null
    private var publishedReviews: MutableList<Review>? = null
    private var followers: MutableList<User>? = null
    private var following: MutableList<User>? = null
    private var roles: MutableList<Role>? = null

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

    fun publishedRecipes(publishedRecipes: MutableList<Recipe>): UserBuilder {
        this.publishedRecipes = publishedRecipes
        return this
    }

    fun savedRecipes(savedRecipes: MutableList<Recipe>): UserBuilder {
        this.savedRecipes = savedRecipes
        return this
    }

    fun publishedReviews(publishedReviews: MutableList<Review>): UserBuilder {
        this.publishedReviews = publishedReviews
        return this
    }

    fun followers(followers: MutableList<User>): UserBuilder {
        this.followers = followers
        return this
    }

    fun following(following: MutableList<User>): UserBuilder {
        this.following = following
        return this
    }

    fun roles(roles: MutableList<Role>): UserBuilder {
        this.roles = roles
        return this
    }

    fun buildFromRegisterRequest(request: RegisterUserRequest): User {
        return User(
            email = request.email,
            password = request.password,
            displayName = request.displayName,
            isAdmin = request.isAdmin
        )
    }

    fun build(): User {
        return User(
            email = email?: throw Exception("Email is required"),
            password = password?: throw Exception("Password is required"),
            displayName = displayName ?: "",
            dateCreated = dateCreated ?: LocalDateTime.now().toString(),
            publishedRecipes = publishedRecipes ?: mutableListOf(),
            savedRecipes = savedRecipes ?: mutableListOf(),
            publishedReviews = publishedReviews ?: mutableListOf(),
            followers = followers ?: mutableListOf(),
            following = following ?: mutableListOf(),
            roles = roles ?: mutableListOf()
        )
    }
}