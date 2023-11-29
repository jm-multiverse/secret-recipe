package jmantello.secretrecipeapi.controller

import jmantello.secretrecipeapi.ResponseEntity.Companion.badRequest
import jmantello.secretrecipeapi.ResponseEntity.Companion.notFound
import jmantello.secretrecipeapi.entity.*
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import jmantello.secretrecipeapi.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val recipeService: RecipeService,
    private val reviewService: ReviewService
) {
    @GetMapping
    fun getUsers(): ResponseEntity<Any> = ok(userService.findAll())

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<out Any> {
        val user = userService.findByIdOrNull(id)
            ?: return notFound("User with id $id not found.")

        return ok(user)
    }

    @PostMapping
    fun createUser(@RequestBody dto: LoginUserDTO): ResponseEntity<out Any> =
        badRequest("New users must be registered through the authentication endpoint: 'api/auth/register'.")

    @PutMapping
    fun updateUser(@RequestBody user: User): ResponseEntity<out Any> {
        if(userService.isEmailRegistered(user.email))
            ok(userService.save(user))

        return badRequest("Email: ${user.email} is not associated with a registered account, so no user exists to be updated.")
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<out Any> =
        ok(userService.deleteById(id))

    @GetMapping("{id}/published-recipes")
    fun getPublishedRecipes(@PathVariable id: Long): ResponseEntity<out Any> {
        val user = userService.findByIdOrNull(id)
            ?: return notFound("User with id $id not found.")

        val recipes = recipeService.findAllPublishedByUserId(user.id)

        return ok(recipes)
    }
    @PostMapping("/{userId}/save-recipe/{recipeId}")
    fun saveRecipe(@PathVariable userId: Long, @PathVariable recipeId: Long): ResponseEntity<out Any> {
        return ok(userService.saveRecipeForUser(userId, recipeId))
    }

    @GetMapping("{id}/saved-recipes")
    fun getSavedRecipes(@PathVariable id: Long): ResponseEntity<out Any> {
        val user = userService.findByIdOrNull(id)
            ?: return notFound("User with id $id not found.")

        val recipes = recipeService.findAllSavedByUserId(user.id)
        return ok(recipes)
    }

    @GetMapping("{id}/reviews")
    fun getPublishedReviews(@PathVariable id: Long): ResponseEntity<out Any> {
        val user = userService.findByIdOrNull(id)
            ?: return notFound("User with id $id not found.")

        val reviews = reviewService.findAllPublishedByUserId(user.id)
        return ok(reviews)
    }
}