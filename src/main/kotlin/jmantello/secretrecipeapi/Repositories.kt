package jmantello.secretrecipeapi

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface RecipeRepository : CrudRepository<Recipe, Long> {
    fun findAllByOrderByAddedAtDesc(): Iterable<Recipe>
}

interface UserRepository : JpaRepository<User, Int> {
    fun findByEmail(email: String): User?
}