package jmantello.secretrecipeapi.controller

import jakarta.servlet.http.HttpServletResponse
import jmantello.secretrecipeapi.service.AuthenticationService
import jmantello.secretrecipeapi.service.TokenService.TokenType.ACCESS
import jmantello.secretrecipeapi.service.TokenService.TokenType.REFRESH
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.transfer.model.UserDTO
import jmantello.secretrecipeapi.transfer.request.RegisterUserRequest
import jmantello.secretrecipeapi.transfer.request.UserLoginRequest
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.Cookie
import jmantello.secretrecipeapi.util.ResponseBuilder.respond
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthenticationController(
    private val userService: UserService,
    private val authenticationService: AuthenticationService
) {

    @PostMapping("register")
    fun registerUser(@RequestBody request: RegisterUserRequest): ResponseEntity<ApiResponse<UserDTO>> =
        respond(userService.register(request))

    @PostMapping("login")
    fun login(
        @RequestBody request: UserLoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<UserDTO>> {
        val authentication = when (val authenticationResult = authenticationService.validateAndIssueTokens(request)) {
            is Success -> authenticationResult.data
            is Error -> return respond(authenticationResult)
        }

        response.addCookie(Cookie.createStandardAccessCookie(authentication.accessToken))
        response.addCookie(Cookie.createStandardRefreshCookie(authentication.refreshToken))

        return respond(Success(authentication.user))
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<ApiResponse<String>> {
        val accessCookie = Cookie.createClear(ACCESS.tokenName)
        val refreshCookie = Cookie.createClear(REFRESH.tokenName)

        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)

        return respond(Success("Logout success"))
    }

    @PostMapping("refresh")
    fun refresh(
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<UserDTO>> {
        val authenticatedUser = when (val authenticationResult = authenticationService.getCurrentAuthenticatedUser()) {
            is Success -> authenticationResult.data
            is Error -> return respond(authenticationResult)
        }

        val authentication = when (val authenticationResult = authenticationService.issueTokens(authenticatedUser)) {
            is Success -> authenticationResult.data
            is Error -> return respond(authenticationResult)
        }

        response.addCookie(Cookie.createStandardAccessCookie(authentication.accessToken))
        response.addCookie(Cookie.createStandardRefreshCookie(authentication.refreshToken))

        return respond(Success(authentication.user))
    }
}