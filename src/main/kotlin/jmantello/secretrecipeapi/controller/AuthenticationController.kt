package jmantello.secretrecipeapi.controller

import jakarta.servlet.http.HttpServletResponse
import jmantello.secretrecipeapi.dto.RegisterUserDTO
import jmantello.secretrecipeapi.dto.LoginUserDTO
import jmantello.secretrecipeapi.entity.UserDTO
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
class AuthenticationController(private val userService: UserService) {

    @PostMapping("register")
    fun registerUser(@RequestBody request: RegisterUserDTO): ResponseEntity<ApiResponse<UserDTO>> =
        respond(userService.register(request))

    @PostMapping("login")
    fun login(
        @RequestBody request: LoginUserDTO,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<String>> {
        return when (val result = userService.login(request)) {
            is Success -> {
                // Perform additional logic, e.g., create and attach a token as a cookie
                // For simplicity, just return a success response here
                respond(Success("Login Success"))
            }

            is Error -> respond(result)
        }
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<ApiResponse<String>> {
        // Remove authorization token here
        return respond(Success("Logout success"))
    }
}