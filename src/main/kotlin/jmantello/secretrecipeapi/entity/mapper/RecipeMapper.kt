package jmantello.secretrecipeapi.entity.mapper

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.RecipeDTO
import org.springframework.stereotype.Component

@Component
object RecipeMapper {

    fun toDto(recipe: Recipe): RecipeDTO {
        return RecipeDTO(
            id = recipe.id,
            datePublished = recipe.datePublished,
            publisherId = recipe.publisher?.id!!,
            title = recipe.title,
            content = recipe.content,
            isPrivate = recipe.isPrivate,
            reviews = recipe.reviews.map { it.id },
            tags = recipe.tags.toList(),
            rating = recipe.calculateOverallRating()
        )
    }
}