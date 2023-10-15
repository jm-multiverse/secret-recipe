package jmantello.secretrecipeapi.repository

import jmantello.secretrecipeapi.entity.Recipe
import org.springframework.data.repository.CrudRepository

interface RecipeRepository : CrudRepository<Recipe, Long> {
    fun findAllByOrderByDatePublishedDesc(): Iterable<Recipe>
}