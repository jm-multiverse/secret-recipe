package jmantello.secretrecipeapi.util

class Endpoints(val host: String, val port: Int) {
    val baseUrl = "$host:$port/api"

    // Auth
    val auth = "$baseUrl/auth"
    val register = "$auth/register"
    val login = "$auth/login"
    val logout = "$auth/logout"

    // Users
    val users = "$baseUrl/users"
    fun getUser(id: Long) = "$users/$id"
    fun updateUser(id: Long) = "$users/$id"
    fun getPublishedRecipes(id: Long) = "$users/$id/published-recipes"
    fun saveRecipe(userId: Long, recipeId: Long) = "$users/$userId/save-recipe/$recipeId"
    fun getSavedRecipes(id: Long) = "$users/$id/saved-recipes"
    fun getPublishedReviews(id: Long) = "$users/$id/published-reviews"
    fun following(id: Long) = "$users/$id/following"
    fun followers(id: Long) = "$users/$id/followers"
    fun follow(userId: Long, targetUserId: Long) = "$users/$userId/follow/$targetUserId"
    fun unfollow(userId: Long, targetUserId: Long) = "$users/$userId/unfollow/$targetUserId"
    fun likeReview(userId: Long, reviewId: Long) = "$users/$userId/like-review/$reviewId"
    fun deleteUser(id: Long) = "$users/$id"

    // Recipes
    val recipes = "$baseUrl/recipes"

    // Reviews
    val reviews = "$baseUrl/reviews"
}
