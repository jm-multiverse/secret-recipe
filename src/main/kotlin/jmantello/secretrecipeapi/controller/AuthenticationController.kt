package jmantello.secretrecipeapi.controller

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jmantello.secretrecipeapi.dto.RegisterUserDTO
import jmantello.secretrecipeapi.dto.LoginUserDTO
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.service.JwtService
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.util.ApiResponse
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

        // TODO: See if I could add roles to the User object, rather than using 'isAdmin' and 'isActive'
        val roles = mutableListOf<String>()
        if (user.isAdmin) roles.add("ROLE_ADMIN")
        if (user.isActive) roles.add("ROLE_USER")

        val tokenResult = jwtService.issueWithRoles(user.id, roles)

        val token = when (tokenResult) {
            is Success -> tokenResult.data
            is Error -> return respond(Error(tokenResult.message))
        }

        val cookie = Cookie("token", token)
        cookie.isHttpOnly = true
        response.addCookie(cookie)

        return respond(tokenResult)
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<ApiResponse<String>> {
        // Remove authorization token here
        return respond(Success("Logout success"))
    }
}