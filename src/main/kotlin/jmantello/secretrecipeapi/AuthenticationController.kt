package jmantello.secretrecipeapi

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthenticationController(private val userService: UserService, private val tokenService: TokenService) {
    @PostMapping("register")
    fun registerUser(@RequestBody dto: UserDTO): ResponseEntity<String> = userService.register(dto)

    @PostMapping("login")
    fun login(@RequestBody dto: UserDTO, response: HttpServletResponse): ResponseEntity<String> {
        val user = userService.login(dto)
            ?: return ResponseEntity.badRequest().build()

        val jwt = tokenService.issue(user.id.toString())
        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true

        return ResponseEntity.ok().build()
    }

}
