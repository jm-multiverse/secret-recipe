package jmantello.secretrecipeapi.util

data class ApiResponse<out T : Any>(
    val data: T? = null,
    val error: String? = null
)