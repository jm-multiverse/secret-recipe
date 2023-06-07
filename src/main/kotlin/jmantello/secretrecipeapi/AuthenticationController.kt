package jmantello.secretrecipeapi

import io.jsonwebtoken.Jwts
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.bridge.Message
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
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
        response.addCookie(cookie)

        return ResponseEntity.ok("Login success")
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any> {
        var cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        response.addCookie(cookie)
        return ResponseEntity.ok("Logout success")
    }

    @GetMapping("account")
    fun account(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        // Choosing 404 not found over 401 unauthenticated to hide endpoint from unauthenticated requests
        if(jwt == null)
            return ResponseEntity.status(404).build()

        val user = tokenService.parse(jwt)
            ?: return ResponseEntity.status(404).build()

        return ResponseEntity.ok(user.email)
    }
}
