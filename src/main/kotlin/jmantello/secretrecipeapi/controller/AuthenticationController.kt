package jmantello.secretrecipeapi.controller

import jakarta.servlet.http.HttpServletResponse
import jmantello.secretrecipeapi.ResponseEntity.Companion.badRequest
import jmantello.secretrecipeapi.ResponseEntity.Companion.created
import jmantello.secretrecipeapi.ResponseEntity.Companion.ok
import jmantello.secretrecipeapi.ResponseEntity.Companion.unauthorized
import jmantello.secretrecipeapi.dto.LoginUserRequest
import jmantello.secretrecipeapi.dto.RegisterUserRequest
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.util.ApiResponse
import jmantello.secretrecipeapi.util.Result.Success
import jmantello.secretrecipeapi.util.Result.Error
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthenticationController(private val userService: UserService) {

    @PostMapping("register")
    fun registerUser(@RequestBody request: RegisterUserRequest): ResponseEntity<ApiResponse<UserDTO>> {
        return when (val result = userService.register(request)) {
            is Success -> created(result.data)
            is Error -> badRequest(result.message)
        }
    }

    @PostMapping("login")
    fun login(@RequestBody request: LoginUserRequest, response: HttpServletResponse): ResponseEntity<ApiResponse<String>> {
        return when (val result = userService.login(request)) {
            is Success -> {
                // Perform additional logic, e.g., create and attach a token as a cookie
                // For simplicity, I'll just return a success response here
                ok("Login success")
            }
            is Error -> unauthorized(result.message)
        }
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<ApiResponse<String>> {
        // Remove authorization token here
        return ok("Logout success")
    }
}