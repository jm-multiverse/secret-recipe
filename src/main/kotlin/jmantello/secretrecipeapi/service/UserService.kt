package jmantello.secretrecipeapi.service

import jakarta.transaction.Transactional
import jmantello.secretrecipeapi.dto.LoginUserDTO
import jmantello.secretrecipeapi.dto.RegisterUserDTO
import jmantello.secretrecipeapi.dto.UpdateUserDTO
import jmantello.secretrecipeapi.entity.RecipeDTO
import jmantello.secretrecipeapi.entity.ReviewDTO
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.entity.builder.UserBuilder
import jmantello.secretrecipeapi.entity.mapper.UserMapper
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.UserRepository
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.recipeNotFoundMessage
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.userDeletedMessage
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.userNotFoundMessage
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) {
    // TODO: Validate DTOs before attempting to save
    // TODO: Add @Transactional annotations to services
    // TODO: Consider extracting error messages

    fun findAll(): Result<List<UserDTO>> =
        Success(userRepository.findAll().map { it.toDTO() })

    fun findById(id: Long): Result<UserDTO> {
        val user = userRepository.findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        return Success(user.toDTO())
    }

    fun findByIdOrNull(id: Long): User? =
        userRepository.findByIdOrNull(id)

    fun findByEmail(email: String): User? =
        userRepository.findByEmail(email)

    @Transactional
    fun update(userId: Long, userDTO: UpdateUserDTO): Result<UserDTO> {
        val foundUser = findByIdOrNull(userId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(userId))

        val user = UserBuilder().buildFromDTO(userDTO, foundUser)
        val result = userRepository.save(user).toDTO()
        return Success(result)
    }

    fun save(user: User): User =
        userRepository.save(user)

    fun deleteById(id: Long): Result<Any> {
        userRepository.deleteById(id)
        return Success(NO_CONTENT, userDeletedMessage(id))
    }

    fun isEmailRegistered(email: String): Boolean =
        userRepository.findByEmail(email) != null

    fun register(request: RegisterUserDTO): Result<UserDTO> {
        if (isEmailRegistered(request.email))
            return Error(
                BAD_REQUEST,
                "Cannot register user because the email provided is already associated with an existing account."
            )

        val user = UserBuilder().buildFromRegisterRequest(request)
        userRepository.save(user)

        return Success(CREATED, UserMapper.toDto(user))
    }

    fun login(request: LoginUserDTO): Result<User> {
        val loginError = Error(UNAUTHORIZED, "Login failed. User not found or incorrect password")

        val user = findByEmail(request.email)
            ?: return loginError

        val authorized = user.validatePassword(request.password)

        return if (authorized)
            Success(user)
        else
            loginError
    }

    @Transactional
    fun saveRecipeForUser(userId: Long, recipeId: Long): Result<List<RecipeDTO>> {
        val user = userRepository.findByIdOrNull(userId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(userId))

        val recipe = recipeRepository.findByIdOrNull(recipeId)
            ?: return Error(NOT_FOUND, recipeNotFoundMessage(recipeId))

        if (!user.savedRecipes.contains(recipe)) {
            user.savedRecipes.add(recipe)
            userRepository.save(user)
        }

        val response = user.savedRecipes.map { it.toDTO() }

        return Success(response)
    }

    fun getPublishedRecipes(userId: Long): Result<List<RecipeDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(userId))

        val recipes = user.getPublishedRecipes().map { it.toDTO() }

        return Success(recipes)
    }

    fun getSavedRecipes(userId: Long): Result<List<RecipeDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(userId))

        val recipes = user.getSavedRecipes().map { it.toDTO() }

        return Success(recipes)
    }

    fun getPublishedReviews(userId: Long): Result<List<ReviewDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(userId))

        val reviews = user.getPublishedReviews().map { it.toDTO() }

        return Success(reviews)
    }
}

