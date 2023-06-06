package jmantello.secretrecipeapi.components.recipe

import org.springframework.data.repository.CrudRepository

interface RecipeRepository : CrudRepository<RecipeModel, Long> {
    fun findAllByOrderByAddedAtDesc(): Iterable<RecipeModel>
}