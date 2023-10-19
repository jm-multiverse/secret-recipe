package jmantello.secretrecipeapi.repository

import jmantello.secretrecipeapi.entity.Recipe
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface RecipeRepository : CrudRepository<Recipe, Long> {
    fun findAllByOrderByDatePublishedDesc(): Iterable<Recipe>

    @Query("SELECT r FROM Recipe r WHERE r.publisher.id = :publisherId")
    fun findAllByPublisherId(publisherId: Long?): MutableList<Recipe>
}