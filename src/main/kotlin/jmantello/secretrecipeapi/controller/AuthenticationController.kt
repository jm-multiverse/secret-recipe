package jmantello.secretrecipeapi.controller

import jakarta.servlet.http.HttpServletResponse
import jmantello.secretrecipeapi.transfer.request.UserLoginRequest
import jmantello.secretrecipeapi.transfer.request.RefreshTokenRequest
import jmantello.secretrecipeapi.transfer.request.RegisterUserRequest
import jmantello.secretrecipeapi.transfer.response.UserLoginResponse
import jmantello.secretrecipeapi.transfer.model.UserDTO
import jmantello.secretrecipeapi.service.TokenService
import jmantello.secretrecipeapi.service.UserService
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
    private val tokenService: TokenService
) {

    @PostMapping("register")
    fun registerUser(@RequestBody request: RegisterUserRequest): ResponseEntity<ApiResponse<UserDTO>> =
        respond(userService.register(request))

    @PostMapping("login")
    fun login(
        @RequestBody request: UserLoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<UserLoginResponse>> {
        val user = when (val authenticationResult = userService.authenticate(request)) {
            is Success -> authenticationResult.data
            is Error -> return respond(Error(authenticationResult.message))
        }

        val accessToken = when (val tokenResult = tokenService.generateAccessToken(user)) {
            is Success -> tokenResult.data
            is Error -> return respond(Error(tokenResult.message))
        }

        val refreshToken = when (val tokenResult = tokenService.generateRefreshToken(user)) {
            is Success -> tokenResult.data
            is Error -> return respond(Error(tokenResult.message))
        }

        val accessTokenExpiryDuration = 3600 // seconds, 1 hour
        val refreshTokenExpiryDuration = 604800 // seconds, 1 week

        response.addCookie(Cookie.create("accessToken", accessToken, accessTokenExpiryDuration))
        response.addCookie(Cookie.create("refreshToken", refreshToken, refreshTokenExpiryDuration))

        val userLoginResponse = UserLoginResponse(user.toDTO(), accessToken, refreshToken)
        return respond(Success(userLoginResponse))
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<ApiResponse<String>> {
        val cookie = Cookie.create("token", "", 0, httpOnly = true, secure = true)
        response.addCookie(cookie)

        return respond(Success("Logout success"))
    }

    @PostMapping("refresh")
    fun refresh(
        request: RefreshTokenRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<String>> {
        val refreshToken = request.refreshToken
        val user = when (val validationResult = tokenService.validate(refreshToken)) {
            is Success -> validationResult.data
            is Error -> return respond(Error(validationResult.message))
        }

        val token = when (val tokenResult = tokenService.generateAccessToken(user)) {
            is Success -> tokenResult.data
            is Error -> return respond(Error(tokenResult.message))
        }

        val cookie = Cookie.create("token", token, 3600, httpOnly = true, secure = true)
        response.addCookie(cookie)

        return respond(Success("Refresh success"))
    }
}