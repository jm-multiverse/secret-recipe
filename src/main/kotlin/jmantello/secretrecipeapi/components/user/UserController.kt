package jmantello.secretrecipeapi.components.user

import jmantello.secretrecipeapi.UserService
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
class UserController(private val service: UserService) {

    @GetMapping // Hide
    fun get(): Iterable<UserModel> = service.get()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): UserModel? = service.getById(id)

    @PostMapping
    fun create(@RequestBody user: UserModel): UserModel = service.create(user)

    @PutMapping
    fun update(@RequestBody user: UserModel): UserModel = service.update(user)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @PostMapping
    fun login(@RequestBody user: UserModel): String = service.login(user)
}