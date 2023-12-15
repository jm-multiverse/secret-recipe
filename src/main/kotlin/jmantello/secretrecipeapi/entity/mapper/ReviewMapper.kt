package jmantello.secretrecipeapi.entity.mapper

import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.ReviewDTO
import jmantello.secretrecipeapi.entity.User
import org.springframework.stereotype.Component

@Component
object ReviewMapper {

    fun toDto(review: Review): ReviewDTO {
        return ReviewDTO(
            id = review.id,
            datePublished = review.datePublished,
            publisherId = review.publisher!!.id,
            title = review.title,
            rating = review.rating,
            content = review.content,
            recipeId = review.recipe!!.id,
            likes = review.likes.map(User::id).toList(),
            isPrivate = review.isPrivate,
        )
    }
}