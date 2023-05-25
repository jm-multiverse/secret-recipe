package jmantello.secretrecipeapi

import org.springframework.data.repository.CrudRepository

interface RecipeRepository : CrudRepository<Recipe, Long> {
    fun findAllByOrderByAddedAtDesc(): Iterable<Recipe>
}