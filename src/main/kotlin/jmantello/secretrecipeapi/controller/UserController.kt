package jmantello.secretrecipeapi.controller

import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.entity.UserDTO
import jmantello.secretrecipeapi.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {
    @GetMapping
    fun getUsers(): ResponseEntity<Any> =
        ResponseEntity.ok(userService.getUsers())

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<Any> {
        val recipe = userService.getUserById(id)
            ?: return ResponseEntity.status(404).body("User with id $id not found.")

        return ResponseEntity.ok(recipe)
    }

    @PostMapping
    fun createUser(@RequestBody dto: UserDTO): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body("New users must be registered through the authentication endpoint: 'api/auth/register'.")
    }

    @PutMapping
    fun updateUser(@RequestBody user: User): ResponseEntity<Any> =
        ResponseEntity.ok(userService.updateUser(user))

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Any> =
        ResponseEntity.ok(userService.deleteUser(id))

}