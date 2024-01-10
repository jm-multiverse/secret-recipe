package jmantello.secretrecipeapi.config.security

import org.springframework.http.HttpMethod
import org.springframework.util.AntPathMatcher

object PublicEndpoints {
    data class Endpoint(val method: HttpMethod = HttpMethod.GET, val path: String)
    private val pathMatcher = AntPathMatcher()

    // TODO: Add request method to the set.
    val PUBLIC_API_ENDPOINTS = listOf(
        Endpoint(HttpMethod.POST, "/api/auth/register"),
        Endpoint(HttpMethod.POST, "/api/auth/login"),
        Endpoint(path = "/actuator"),
        Endpoint(path = "/actuator/**"),
        Endpoint(path = "/favicon.ico")
    )

    fun isPublic(requestPath: String, requestMethod: String): Boolean {
        return PUBLIC_API_ENDPOINTS.any { endpoint ->
            pathMatcher.match(endpoint.path, requestPath) && endpoint.method == HttpMethod.valueOf(requestMethod)
        }
    }
}