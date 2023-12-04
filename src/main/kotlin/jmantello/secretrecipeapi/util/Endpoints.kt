package jmantello.secretrecipeapi.util

class Endpoints(val host: String, val port: Int) {
    val baseUrl = "$host:$port"

    // Auth
    val auth = "$baseUrl/api/auth"
    val register = "$auth/register"
    val login = "$auth/login"
    val logout = "$auth/logout"

    // Users
    val users = "$baseUrl/api/users"
    fun getUser(id: Long) = "$users/$id"
    fun getPublishedRecipes(userId: Long) = "$users/$userId/published-recipes"
    fun saveRecipe(userId: Long, recipeId: Long) = "$users/$userId/save-recipe/$recipeId"
    fun getSavedRecipes(userId: Long) = "$users/$userId/saved-recipes"
    fun getPublishedReviews(userId: Long) = "$users/$userId/published-reviews"

    // Recipes
    val recipes = "$baseUrl/api/recipes"

    // Reviews
    val reviews = "$baseUrl/api/reviews"
}
