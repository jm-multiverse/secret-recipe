package jmantello.secretrecipeapi

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthenticationController(private val userService: UserService) {
    @PostMapping("register")
    fun registerUser(@RequestBody dto: UserDTO): ResponseEntity<String> = userService.register(dto)

    @PostMapping("login")
    fun login(@RequestBody dto: UserDTO): ResponseEntity<String> = userService.login(dto)

}
