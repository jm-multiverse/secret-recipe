package jmantello.secretrecipeapi.service

import jakarta.transaction.Transactional
import jmantello.secretrecipeapi.dto.LoginUserRequest
import jmantello.secretrecipeapi.dto.RegisterUserRequest
import jmantello.secretrecipeapi.dto.UpdateUserRequest
import jmantello.secretrecipeapi.entity.*
import jmantello.secretrecipeapi.entity.builder.UserBuilder
import jmantello.secretrecipeapi.entity.mapper.UserMapper
import jmantello.secretrecipeapi.exception.ResourceNotFoundException
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.UserRepository
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

class UserNotFoundException(userId: Long) : ResourceNotFoundException("User with ID $userId not found")

@Service
class UserService(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) {
    // TODO: Validate DTOs before attempting to save it
    // TODO: Add @Transactional annotations to services
    fun userNotFound(userId: Long) = "User with ID $userId not found."
    fun recipeNotFound(recipeId: Long) = "Recipe with ID $recipeId not found."

    fun findAll(): Result<List<UserDTO>> =
        Success(userRepository.findAll().map { it.toDTO() })

    fun findById(id: Long): Result<UserDTO> {
        val user = userRepository.findByIdOrNull(id)
            ?: return Error("User with id: $id not found.")

        return Success(UserMapper.toDto(user))
    }

    fun findByIdOrNull(id: Long): User? = userRepository.findByIdOrNull(id)
    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    @Transactional
    fun update(userId:Long, userDTO: UpdateUserRequest): Result<UserDTO> {
        val foundUser = findByIdOrNull(userId)
            ?: return Error(userNotFound(userId))

        val user = UserBuilder().buildFromDTO(userDTO, foundUser)
        val result = userRepository.save(user).toDTO()
        return Success(result)
    }

    fun save(user: User): User = userRepository.save(user)
    fun deleteById(id: Long): Result<Any> {
        userRepository.deleteById(id)
        return Success("Successfully deleted user with ID: $id")
    }
    fun isEmailRegistered(email: String): Boolean =
        userRepository.findByEmail(email) != null

    fun register(request: RegisterUserRequest): Result<UserDTO> {
        if (isEmailRegistered(request.email))
            return Error("Cannot register user because the email provided is already associated with an existing account.")

        val user = UserBuilder().buildFromRegisterRequest(request)
        userRepository.save(user)

        return Success(UserMapper.toDto(user))
    }

    fun login(request: LoginUserRequest): Result<User> {
        val errorMessage = "Login failed. User not found or incorrect password"

        val user = findByEmail(request.email)
            ?: return Error(errorMessage)

        val authorized = user.validatePassword(request.password)

        return if (authorized)
            Success(user)
        else
            Error(errorMessage)
    }

    fun saveRecipeForUser(userId: Long, recipeId: Long): Result<List<RecipeDTO>> {
        val user = userRepository.findByIdOrNull(userId)
            ?: return Error(userNotFound(userId))

        val recipe = recipeRepository.findByIdOrNull(recipeId)
            ?: return Error(recipeNotFound(recipeId))

        if (!user.savedRecipes.contains(recipe)) {
            user.savedRecipes.add(recipe)
            userRepository.save(user)
        }

        val response = user.savedRecipes.map { it.toDTO() }

        return Success(response)
    }

    fun getPublishedRecipes(userId: Long): Result<List<RecipeDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(userNotFound(userId))

        val recipes = user.getPublishedRecipes().map { it.toDTO() }

        return Success(recipes)
    }

    fun getSavedRecipes(userId: Long): Result<List<RecipeDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(userNotFound(userId))

        val recipes = user.getSavedRecipes().map { it.toDTO() }

        return Success(recipes)
    }

    fun getPublishedReviews(userId: Long): Result<List<ReviewDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(userNotFound(userId))

        val reviews = user.getPublishedReviews().map { it.toDTO() }

        return Success(reviews)
    }
}

