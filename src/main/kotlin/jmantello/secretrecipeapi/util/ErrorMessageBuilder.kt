package jmantello.secretrecipeapi.util

import kotlin.reflect.KClass

object ErrorMessageBuilder {
    fun <T : Any> notFoundMessage(entityClass: KClass<T>, id: Long): String {
        val entityName = entityClass.simpleName
        return "$entityName with ID: $id not found."
    }
    fun userNotFoundMessage(userId: Long) = "User with ID $userId not found."
    fun recipeNotFoundMessage(recipeId: Long) = "Recipe with ID $recipeId not found."
    fun reviewNotFoundMessage(reviewId: Long) = "Review with ID $reviewId not found."

}