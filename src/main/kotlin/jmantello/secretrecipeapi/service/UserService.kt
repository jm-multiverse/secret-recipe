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
import jmantello.secretrecipeapi.repository.ReviewRepository
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
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository,
) {
    // TODO: Validate DTOs before attempting to save
    // TODO: Add @Transactional annotations to services

    fun findAll(): Result<List<UserDTO>> =
        Success(userRepository.findAll().map { it.toDTO() })

    @Transactional
    fun findById(id: Long): Result<UserDTO> {
        val user = userRepository.findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        return Success(user.toDTO())
    }

    @Transactional
    fun findByIdOrNull(id: Long): User? =
        userRepository.findByIdOrNull(id)

    fun findByEmail(email: String): User? =
        userRepository.findByEmail(email)

    @Transactional
    fun update(id: Long, userDTO: UpdateUserDTO): Result<UserDTO> {
        val foundUser = findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        foundUser.update(userDTO)
        val result = userRepository.save(foundUser).toDTO()

        return Success(result)
    }

    fun save(user: User): User =
        userRepository.save(user)

    @Transactional
    fun deleteById(id: Long): Result<Any> {
        val user = findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        user.isActive = false
        userRepository.save(user)
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

    @Transactional
    fun getFollowers(id: Long): Result<List<UserDTO>> {
        val user = findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        val followers = user.followers.map { it.toDTO() }

        return Success(followers)
    }


    @Transactional
    fun getFollowing(id: Long): Result<List<UserDTO>> {
        val user = findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        val following = user.following.map { it.toDTO() }

        return Success(following)
    }

    @Transactional
    fun follow(userId: Long, targetUserId: Long): Result<List<UserDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(userId))

        val targetUser = findByIdOrNull(targetUserId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(targetUserId))

        user.follow(targetUser)
        val response = user.following.map { it.toDTO() }
        return Success(response)
    }

    @Transactional
    fun unfollow(userId: Long, targetUserId: Long): Result<List<UserDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(userId))

        val targetUser = findByIdOrNull(targetUserId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(targetUserId))

        user.unfollow(targetUser)
        val response = user.followers.map { it.toDTO() }
        return Success(response)
    }

    @Transactional
    fun likeReview(userId: Long, reviewId: Long): Result<List<ReviewDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(userId))

        val review = reviewRepository.findByIdOrNull(reviewId)
            ?: return Error(NOT_FOUND, recipeNotFoundMessage(reviewId))

        user.likeReview(review)
        val response = user.likedReviews.map { it.toDTO() }
        return Success(response)
    }
}

