package jmantello.secretrecipeapi.controller

import jakarta.servlet.http.HttpServletResponse
import jmantello.secretrecipeapi.ResponseEntity.Companion.badRequest
import jmantello.secretrecipeapi.ResponseEntity.Companion.created
import jmantello.secretrecipeapi.entity.LoginUserDTO
import jmantello.secretrecipeapi.entity.RegisterUserDTO
import jmantello.secretrecipeapi.service.UserService
import jmantello.secretrecipeapi.service.Result
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
    fun registerUser(@RequestBody dto: RegisterUserDTO): ResponseEntity<out Any> {
        return when (val result = userService.register(dto)) {
            is Result.Success -> created(result.data)
            is Result.Error -> badRequest(result.message)
        }
    }

    @PostMapping("login")
    fun login(@RequestBody dto: LoginUserDTO, response: HttpServletResponse): ResponseEntity<String> {
        return when (val result = userService.login(dto)) {
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