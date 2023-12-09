package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.dto.LoginRequest
import jmantello.secretrecipeapi.dto.RegisterUserRequest
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.entity.builder.UserBuilder
import jmantello.secretrecipeapi.entity.mapper.UserMapper
import jmantello.secretrecipeapi.exception.ResourceNotFoundException
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.UserRepository
import jmantello.secretrecipeapi.util.Result
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

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

    fun register(request: RegisterUserRequest): Result<UserDTO> {
        if (isEmailRegistered(request.email)) {
            return Result.Error("Cannot register user because email is associated with an existing account.")
        }

        val user = UserBuilder().buildFromRegisterRequest(request)

        userRepository.save(user)

        return Result.Success(UserMapper.toDto(user))
    }

    fun login(request: LoginRequest): Result<User> {
        val user = findByEmail(request.email)
            ?: return Result.Error("User not found")

        val authorized = user.validatePassword(request.password)

        return if (authorized) {
            Result.Success(user)
        } else {
            Result.Error("Invalid password")
        }
    }

    fun saveRecipeForUser(userId: Long, recipeId: Long): List<Recipe> {
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException(userId) }
        val recipe = recipeRepository.findById(recipeId).orElseThrow { RecipeNotFoundException(recipeId) }

        if (!user.savedRecipes.contains(recipe)) {
            user.savedRecipes.add(recipe)
            userRepository.save(user)
        }

        return user.savedRecipes
    }
}

