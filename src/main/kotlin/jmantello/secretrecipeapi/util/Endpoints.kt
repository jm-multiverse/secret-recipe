package jmantello.secretrecipeapi.util

class Endpoints(val host: String, val port: Int) {
    val baseUrl = "$host:$port/api"

    // Auth
    val auth = "$baseUrl/auth"
    val register = "$auth/register"
    val login = "$auth/login"
    val refresh = "$auth/refresh"
    val logout = "$auth/logout"

    // Users
    val users = "$baseUrl/users"
    fun getUser(id: Long) = "$users/$id"
    fun updateUser(id: Long) = "$users/$id"
    fun getPublishedRecipes(id: Long) = "$users/$id/published-recipes"
    fun getSavedRecipes(id: Long) = "$users/$id/saved-recipes"
    fun getPublishedReviews(id: Long) = "$users/$id/published-reviews"
    fun following(id: Long) = "$users/$id/following"
    fun followers(id: Long) = "$users/$id/followers"
    fun follow(userId: Long, targetUserId: Long) = "$users/$userId/follow/$targetUserId"
    fun unfollow(userId: Long, targetUserId: Long) = "$users/$userId/unfollow/$targetUserId"
    fun deleteUser(id: Long) = "$users/$id"

    // Recipes
    val recipes = "$baseUrl/recipes"
    fun getRecipe(id: Long) = "$recipes/$id"
    fun saveRecipe(recipeId: Long) = "$recipes/$recipeId/save"
    fun unsaveRecipe(recipeId: Long) = "$recipes/$recipeId/unsave"
    fun getRecipeReviews(recipeId: Long) = "$recipes/$recipeId/reviews"
    fun publishReview(recipeId: Long) = "$recipes/$recipeId/reviews"

    // Reviews
    val reviews = "$baseUrl/reviews"
    fun getReview(id: Long) = "$reviews/$id"
    fun likeReview(reviewId: Long) = "$reviews/$reviewId/like"
    fun unlikeReview(reviewId: Long) = "$reviews/$reviewId/unlike"

}
