package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun getUsers(): Iterable<User> = userRepository.findAll()
    fun getUserById(id: Long): User? = userRepository.findByIdOrNull(id)
    fun updateUser(user: User): User = userRepository.save(user)
    fun deleteUser(id: Long): Unit = userRepository.deleteById(id)
    fun emailRegistered(email: String): Boolean = userRepository.findByEmail(email) != null
    fun findByEmail(email: String): User? = userRepository.findByEmail(email)
    fun findByIdOrNull(id: Long): User? = userRepository.findByIdOrNull(id)

    fun register(dto: UserDTO): Result<User> {
        if (emailRegistered(dto.email)) {
            return Result.Error("New users must not use an email associated with an existing account")
        }

        val user = User()
        user.email = dto.email
        user.password = dto.password
        userRepository.save(user)

        return Result.Success(user)
    }

    fun login(dto: UserDTO): Result<User> {
        val user = findByEmail(dto.email)
            ?: return Result.Error("User not found")

        val authorized = user.validatePassword(dto.password)

        return if (authorized) {
            Result.Success(user)
        } else {
            Result.Error("Invalid password")
        }
    }
}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}