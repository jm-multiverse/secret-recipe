package jmantello.secretrecipeapi.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jmantello.secretrecipeapi.service.TokenService
import jmantello.secretrecipeapi.service.TokenService.TokenType.ACCESS
import jmantello.secretrecipeapi.service.TokenService.TokenType.REFRESH
import jmantello.secretrecipeapi.util.ErrorFactory.Companion.unauthorizedError
import jmantello.secretrecipeapi.util.ResponseBuilder.respond
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtAuthenticationFilter(
    private val tokenService: TokenService,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    private fun shouldSkipFilter(request: HttpServletRequest): Boolean {
        val skipPaths = setOf("/api/auth/register", "/api/auth/login")
        val requestPath = request.servletPath
        return skipPaths.contains(requestPath)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (shouldSkipFilter(request)) {
            filterChain.doFilter(request, response)
            return
        }

        // All requests except /login and /register come through this filter
        // and carry either an access or a refresh token.

        // We'll handle generating tokens in the authentication service.
        // We'll only need to handle:
        // 1. Extract either an access or refresh token
        // 2. Authenticate it, and set the context to the current user.

        val token = when (val result = extractToken(request)) {
            is Success -> result.data
            is Error -> {
                writeUnauthorizedError(response)
                return
            }
        }

        try {
            val authentication = tokenService.authenticate(token)
            setAuthenticationContext(authentication)
        } catch (ex: Exception) {
            writeUnauthorizedError(response)
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): Result<String> {
        // Find access token first, then refresh, since both may be present, this might cause an issue.
        val cookie = request.cookies?.find { it.name == ACCESS.tokenName }
            ?: request.cookies?.find { it.name == REFRESH.tokenName }
            ?: return Error("No token found in cookies")

        val token = cookie.value
            ?: return Error("Token value is empty")

        return Success(token)
    }

    private fun setAuthenticationContext(authentication: Authentication) {
        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun writeUnauthorizedError(response: HttpServletResponse) {
        SecurityContextHolder.clearContext()
        val apiResponse = respond(unauthorizedError)
        response.writer.write(objectMapper.writeValueAsString(apiResponse))
    }
}
