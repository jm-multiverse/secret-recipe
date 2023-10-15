package jmantello.secretrecipeapi.repository

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.User
import org.springframework.data.repository.CrudRepository

interface RecipeRepository : CrudRepository<Recipe, Long> {
    fun findAllByOrderByAddedAtDesc(): Iterable<Recipe>
}

interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): User?
}