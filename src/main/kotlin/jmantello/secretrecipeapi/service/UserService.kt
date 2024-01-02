package jmantello.secretrecipeapi.service

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jmantello.secretrecipeapi.transfer.model.RecipeDTO
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.entity.User.Status.ACTIVE
import jmantello.secretrecipeapi.entity.User.Status.SOFT_DELETED
import jmantello.secretrecipeapi.transfer.model.UserDTO
import jmantello.secretrecipeapi.entity.builder.UserBuilder
import jmantello.secretrecipeapi.entity.filters.ActiveUsersFilter
import jmantello.secretrecipeapi.entity.mapper.UserMapper
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.ReviewRepository
import jmantello.secretrecipeapi.repository.UserRepository
import jmantello.secretrecipeapi.transfer.request.RegisterUserRequest
import jmantello.secretrecipeapi.transfer.request.UpdateUserRequest
import jmantello.secretrecipeapi.transfer.request.UserLoginRequest
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.recipeNotFoundMessage
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.userDeletedMessage
import jmantello.secretrecipeapi.util.ErrorMessageBuilder.userNotFoundMessage
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.hibernate.Session
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository,
    private val entityManager: EntityManager,
) {
    private val session: Session
        get() = entityManager.unwrap(Session::class.java)

    fun findAll(): Result<List<UserDTO>> =
        Success(userRepository.findAll().map { it.toDTO() })

    fun findActive(): Result<List<UserDTO>> {
        session
            .enableFilter(ActiveUsersFilter.NAME)
            .setParameter("userStatus", ACTIVE)

        val result = userRepository.findAll().map { it.toDTO() }

        session.disableFilter(ActiveUsersFilter.NAME)

        return Success(result)
    }

    fun findById(id: Long): Result<UserDTO> {
        val user = userRepository.findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        return Success(user.toDTO())
    }

    fun findByIdOrNull(id: Long): User? =
        userRepository.findByIdOrNull(id)

    fun findByEmail(email: String): User? =
        userRepository.findByEmail(email)

    fun update(id: Long, userDTO: UpdateUserRequest): Result<UserDTO> {
        val foundUser = findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        foundUser.update(userDTO)
        val result = userRepository.save(foundUser).toDTO()

        return Success(result)
    }

    fun save(user: User): User =
        userRepository.save(user)

    fun deleteById(id: Long): Result<Any> {
        val user = findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        user.status = SOFT_DELETED
        userRepository.save(user)
        return Success(NO_CONTENT, userDeletedMessage(id))
    }

    fun isEmailRegistered(email: String): Boolean =
        userRepository.findByEmail(email) != null

    fun register(request: RegisterUserRequest): Result<UserDTO> {
        if (isEmailRegistered(request.email))
            return Error(
                BAD_REQUEST,
                "Cannot register user because the email provided is already associated with an existing account."
            )

        val user = UserBuilder().buildFromRegisterRequest(request)
        userRepository.save(user)

        return Success(CREATED, UserMapper.toDto(user))
    }

    fun authenticate(request: UserLoginRequest): Result<User> {
        val unauthenticatedError = Error(UNAUTHORIZED, "Login failed. User not found or incorrect password")

        val user = findByEmail(request.email)
            ?: return unauthenticatedError

        val authorized = user.validatePassword(request.password)

        return if (authorized)
            Success(user)
        else
            unauthenticatedError
    }

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

    fun getFollowers(id: Long): Result<List<UserDTO>> {
        val user = findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        val followers = user.followers.map { it.toDTO() }

        return Success(followers)
    }


    fun getFollowing(id: Long): Result<List<UserDTO>> {
        val user = findByIdOrNull(id)
            ?: return Error(NOT_FOUND, userNotFoundMessage(id))

        val following = user.following.map { it.toDTO() }

        return Success(following)
    }

    fun follow(userId: Long, targetUserId: Long): Result<List<UserDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(userId))

        val targetUser = findByIdOrNull(targetUserId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(targetUserId))

        user.follow(targetUser)
        val response = user.following.map { it.toDTO() }
        return Success(response)
    }

    fun unfollow(userId: Long, targetUserId: Long): Result<List<UserDTO>> {
        val user = findByIdOrNull(userId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(userId))

        val targetUser = findByIdOrNull(targetUserId)
            ?: return Error(NOT_FOUND, userNotFoundMessage(targetUserId))

        user.unfollow(targetUser)
        val response = user.following.map { it.toDTO() }
        return Success(response)
    }

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

