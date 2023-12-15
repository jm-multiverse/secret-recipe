package jmantello.secretrecipeapi.entity.builder

import jmantello.secretrecipeapi.dto.RegisterUserDTO
import jmantello.secretrecipeapi.dto.SaveUserDTO
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

    fun buildFromRegisterRequest(request: RegisterUserDTO): User {
        return User().apply {
            email = request.email
            password = request.password
            request.displayName?.let { displayName = it }
            request.isAdmin?.let { isAdmin = it }
        }
    }

    fun buildFromDTO(userDTO: SaveUserDTO, user: User): User {
        return user.apply {
            userDTO.email?.let { this.email = it }
            userDTO.password?.let { this.password = it }
            userDTO.displayName?.let { this.displayName = it }
            userDTO.isAdmin?.let { this.isAdmin = it }
        }
    }

    fun build(): User {
        return user
    }
}