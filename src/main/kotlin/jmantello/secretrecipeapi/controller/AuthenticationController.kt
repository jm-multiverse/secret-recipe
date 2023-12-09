package jmantello.secretrecipeapi.controller

import jakarta.servlet.http.HttpServletResponse
import jmantello.secretrecipeapi.ResponseEntity.Companion.badRequest
import jmantello.secretrecipeapi.ResponseEntity.Companion.created
import jmantello.secretrecipeapi.dto.LoginRequest
import jmantello.secretrecipeapi.dto.RegisterUserRequest
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthenticationController(private val userService: UserService) {
    @PostMapping("register")
    fun registerUser(@RequestBody request: RegisterUserRequest): ResponseEntity<UserDTO> {
        return when (val result = userService.register(request)) {
            is Result.Success -> created(result.data)
            is Result.Error -> badRequest(result.message)
        }
    }

    @PostMapping("login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<String> {
        return when (val result = userService.login(request)) {
            is Result.Success -> {
                // Perform additional logic, e.g., create and attach a token as a cookie
                // For simplicity, I'll just return a success response here
                ok("Login success")
            }
            is Result.Error -> badRequest(result.message)
        }
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any> {
        // Remove authorization token here
        return ok("Logout success")
    }
}