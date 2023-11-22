package jmantello.secretrecipeapi.controller

import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.entity.LoginUserDTO
import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
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
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val recipeService: RecipeService,
    private val reviewService: ReviewService
) {
    @GetMapping
    fun getUsers(): ResponseEntity<Any> =
        ResponseEntity.ok(userService.findAll())

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<Any> {
        val user = userService.findByIdOrNull(id)
            ?: return ResponseEntity.status(404).body("User with id $id not found.")

        return ResponseEntity.ok(user)
    }

    @PostMapping
    fun createUser(@RequestBody dto: LoginUserDTO): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body("New users must be registered through the authentication endpoint: 'api/auth/register'.")
    }

    @PutMapping
    fun updateUser(@RequestBody user: User): ResponseEntity<Any> {
        if(userService.isEmailRegistered(user.email))
            ResponseEntity.ok(userService.save(user))

        return ResponseEntity.badRequest().body("Email: ${user.email} is not associated with a registered account, so no user exists to be updated.")
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Any> =
        ResponseEntity.ok(userService.deleteById(id))

    @GetMapping("{id}/published-recipes")
    fun getPublishedRecipes(@PathVariable id: Long): ResponseEntity<List<Recipe>?> {
        val user = userService.findByIdOrNull(id)
            ?: return ResponseEntity.notFound().build()

        val recipes = recipeService.findAllPublishedByUserId(user.id)
        return ResponseEntity.ok(recipes)
    }
    @PostMapping("/api/users/{userId}/saveRecipe/{recipeId}")
    fun saveRecipe(@PathVariable userId: Long, @PathVariable recipeId: Long) {
        userService.saveRecipeForUser(userId, recipeId)
    }

    @GetMapping("{id}/saved-recipes")
    fun getSavedRecipes(@PathVariable id: Long): ResponseEntity<List<Recipe>?> {
        val user = userService.findByIdOrNull(id)
            ?: return ResponseEntity.notFound().build()

        val recipes = recipeService.findAllSavedByUserId(user.id)
        return ResponseEntity.ok(recipes)
    }

    @GetMapping("{id}/reviews")
    fun getPublishedReviews(@PathVariable id: Long): ResponseEntity<List<Review>?> {
        val user = userService.findByIdOrNull(id)
            ?: return ResponseEntity.notFound().build()

        val reviews = reviewService.findAllByUserId(user.id)
        return ResponseEntity.ok(reviews)
    }


}