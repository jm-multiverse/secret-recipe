package jmantello.secretrecipeapi

import io.jsonwebtoken.Jwts
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
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

    @GetMapping("account")
    fun account(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        try {
            if(jwt == null)
                return ResponseEntity.badRequest().build()

            val body = Jwts.parser().setSigningKey("secret-key").parseClaimsJws(jwt).body
            val userId = body.issuer.toLong()
            val user = userService.findByIdOrNull(userId)
                ?: return ResponseEntity.badRequest().build()

            return ResponseEntity.ok(user.email)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }
}
