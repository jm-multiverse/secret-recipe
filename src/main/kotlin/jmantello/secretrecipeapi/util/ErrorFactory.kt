package jmantello.secretrecipeapi.util

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.util.Result.Error
import org.springframework.http.HttpStatus.*
import kotlin.reflect.KClass

class ErrorFactory {

    companion object {
        const val unauthorizedMessage: String = "Access denied. Please verify your credentials."
        val unauthorizedError: Error = Error(UNAUTHORIZED, unauthorizedMessage)

        const val userAlreadyRegisteredWithEmailMessage: String = "Cannot register user. Email already registered."
        val userAlreadyRegisteredWithEmailError: Error = Error(
            CONFLICT,
            userAlreadyRegisteredWithEmailMessage
        )

        fun <T : Any> entityNotFoundError(entityClass: KClass<T>, id: Long): Error {
            val entityName = entityClass.simpleName ?: "Entity"
            val message = "$entityName with ID: $id not found."
            return Error(NOT_FOUND, message)
        }

        fun userNotFoundError(userId: Long) = entityNotFoundError(User::class, userId)
        fun recipeNotFoundError(recipeId: Long) = entityNotFoundError(Recipe::class, recipeId)
        fun reviewNotFoundError(reviewId: Long) = entityNotFoundError(Review::class, reviewId)

    }
}