package jmantello.secretrecipeapi.controller

import jakarta.servlet.http.HttpServletResponse
import jmantello.secretrecipeapi.dto.LoginUserDTO
import jmantello.secretrecipeapi.dto.RegisterUserDTO
import jmantello.secretrecipeapi.entity.Role
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.service.JwtService
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
    private val jwtService: JwtService
) {

    @PostMapping("register")
    fun registerUser(@RequestBody request: RegisterUserDTO): ResponseEntity<ApiResponse<UserDTO>> =
        respond(userService.register(request))

    @PostMapping("login")
    fun login(
        @RequestBody request: LoginUserDTO,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<String>> {

        val authenticationResult = userService.authenticate(request)

        val user = when (authenticationResult) {
            is Success -> authenticationResult.data
            is Error -> return respond(Error(authenticationResult.message))
        }

        // TODO: See if I could add roles to the User object elsewhere
        if (user.isAdmin) user.roles.add(Role.ADMIN)
        if (user.isActive) user.roles.add(Role.USER)

        val tokenResult = jwtService.generateAccessToken(user)

        val token = when (tokenResult) {
            is Success -> tokenResult.data
            is Error -> return respond(Error(tokenResult.message))
        }

        val cookie = Cookie.create("token", token, 3600, httpOnly = true, secure = true)
        response.addCookie(cookie)

        return respond(tokenResult)
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<ApiResponse<String>> {
        // Remove authorization token here
        return respond(Success("Logout success"))
    }

    @PostMapping("refresh")
    fun refresh(
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<String>> {
        // Refresh authorization token here
        return respond(Success("Refresh success"))
    }
}