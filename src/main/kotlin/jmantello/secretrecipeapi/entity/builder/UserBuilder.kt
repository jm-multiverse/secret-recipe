package jmantello.secretrecipeapi.entity.builder

import jmantello.secretrecipeapi.dto.RegisterUserRequest
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
            password = request.password // Remember to handle password hashing appropriately
            displayName = request.displayName
        }
    }

    fun build(): User {
        return user
    }
}