package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.entity.LoginUserDTO
import jmantello.secretrecipeapi.entity.RegisterUserDTO
import jmantello.secretrecipeapi.exception.ResourceNotFoundException
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

class UserNotFoundException(userId: Long) : ResourceNotFoundException("User with ID $userId not found")

@Service
class UserService(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) {

    fun findAll(): Iterable<User> = userRepository.findAll()
    fun findByIdOrNull(id: Long): User? = userRepository.findByIdOrNull(id)
    fun findByEmail(email: String): User? = userRepository.findByEmail(email)
    fun save(user: User): User = userRepository.save(user)
    fun deleteById(id: Long): Unit = userRepository.deleteById(id)
    fun isEmailRegistered(email: String): Boolean =
        userRepository.findByEmail(email) != null

    fun register(dto: RegisterUserDTO): Result<User> {
        if (isEmailRegistered(dto.email)) {
            return Result.Error("New users must not use an email associated with an existing account")
        }

        val user = User()
        user.email = dto.email
        user.password = dto.password
        user.displayName = dto.displayName
        userRepository.save(user)

        return Result.Success(user)
    }

    fun login(dto: LoginUserDTO): Result<User> {
        val user = findByEmail(dto.email)
            ?: return Result.Error("User not found")

        val authorized = user.validatePassword(dto.password)

        return if (authorized) {
            Result.Success(user)
        } else {
            Result.Error("Invalid password")
        }
    }

    fun saveRecipeForUser(userId: Long, recipeId: Long) {
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException(userId) }
        val recipe = recipeRepository.findById(recipeId).orElseThrow { RecipeNotFoundException(recipeId) }

        if (!user.savedRecipes.contains(recipe)) {
            user.savedRecipes.add(recipe)
            userRepository.save(user)
        }
    }
}

