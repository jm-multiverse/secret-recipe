package jmantello.secretrecipeapi.util

class EndpointBuilder(val host: String, val port: Int) {
    val baseUrl = "$host:$port"

    // Auth
    val auth = "$baseUrl/api/auth"
    val register = "$auth/register"
    val login = "$auth/login"
    val logout = "$auth/logout"

    // Users
    val users = "$baseUrl/api/users"
    fun publishedRecipes(publisherId: Long) = "$users/$publisherId/published-recipes"

    // Recipes
    val recipes = "$baseUrl/api/recipes"
}