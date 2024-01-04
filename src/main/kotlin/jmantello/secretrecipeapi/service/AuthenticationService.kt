package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.transfer.request.UserLoginRequest
import jmantello.secretrecipeapi.transfer.response.UserAuthenticatedResponse
import jmantello.secretrecipeapi.util.ErrorFactory.Companion.unauthorizedError
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val tokenService: TokenService,
    private val userService: UserService,
) {
    fun validateAndIssue(request: UserLoginRequest): Result<UserAuthenticatedResponse> {
        val user = when (val authenticationResult = userService.validateCredentials(request)) {
            is Success -> authenticationResult.data
            is Error -> return authenticationResult
        }

        return issueTokens(user)
    }

    fun issueTokens(user: User): Result<UserAuthenticatedResponse> {
        val accessToken = when (val tokenResult = tokenService.generateAccessToken(user)) {
            is Success -> tokenResult.data
            is Error -> return tokenResult
        }

        val refreshToken = when (val tokenResult = tokenService.generateRefreshToken(user)) {
            is Success -> tokenResult.data
            is Error -> return tokenResult
        }

        val response = UserAuthenticatedResponse(user.toDTO(), accessToken, refreshToken)

        return Success(response)
    }

    fun getCurrentAuthenticatedUser(): Result<User> {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            return unauthorizedError
        }
        val user = (authentication.principal as User)

        return Success(user)
    }
}