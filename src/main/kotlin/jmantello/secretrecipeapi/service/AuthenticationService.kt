package jmantello.secretrecipeapi.service

import jakarta.transaction.Transactional
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.transfer.model.UserDTO
import jmantello.secretrecipeapi.transfer.request.UserLoginRequest
import jmantello.secretrecipeapi.transfer.response.UserAuthenticatedResponse
import jmantello.secretrecipeapi.util.ErrorResponses.Companion.unauthorizedError
import jmantello.secretrecipeapi.util.ErrorResponses.Companion.userNotFoundError
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
    fun validateAndIssueTokens(request: UserLoginRequest): Result<UserAuthenticatedResponse> {
        val user = when (val authenticationResult = userService.validateCredentials(request)) {
            is Success -> authenticationResult.data
            is Error -> return authenticationResult
        }

        return issueTokens(user)
    }

    fun issueTokens(userDTO: UserDTO): Result<UserAuthenticatedResponse> {
        val accessToken = when (val tokenResult = tokenService.generateAccessToken(userDTO)) {
            is Success -> tokenResult.data
            is Error -> return tokenResult
        }

        val refreshToken = when (val tokenResult = tokenService.generateRefreshToken(userDTO)) {
            is Success -> tokenResult.data
            is Error -> return tokenResult
        }

        val response = UserAuthenticatedResponse(userDTO, accessToken, refreshToken)

        return Success(response)
    }

    fun getPrincipal(): Result<UserDTO> {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            return unauthorizedError
        }

        val userDTO = (authentication.principal as UserDTO)
        return Success(userDTO)
    }

    fun getCurrentUserDTO(): Result<UserDTO> = getPrincipal()

    fun getCurrentUserId(): Result<Long> {
        val principal = when (val principalResult = getPrincipal()) {
            is Success -> principalResult.data
            is Error -> return principalResult
        }

        return Success(principal.id)
    }

    @Transactional
    fun getCurrentUserEntity(): Result<User> {
        val userDTO = when (val principalResult = getPrincipal()) {
            is Success -> principalResult.data
            is Error -> return principalResult
        }

        val user = userService.findByIdOrNull(userDTO.id)
            ?: return userNotFoundError(userDTO.id)

        return Success(user)
    }
}