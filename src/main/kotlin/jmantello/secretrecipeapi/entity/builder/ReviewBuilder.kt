package jmantello.secretrecipeapi.entity.builder

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.User

class ReviewBuilder {
    private val review = Review()

    fun publisher(publisher: User): ReviewBuilder {
        review.publisher = publisher
        return this
    }

    fun recipe(recipe: Recipe): ReviewBuilder {
        review.recipe = recipe
        return this
    }

    fun title(title: String): ReviewBuilder {
        review.title = title
        return this
    }

    fun content(content: String?): ReviewBuilder {
        review.content = content
        return this
    }

    fun rating(rating: Double): ReviewBuilder {
        review.rating = rating
        return this
    }

    fun isPrivate(isPrivate: Boolean): ReviewBuilder {
        review.isPrivate = isPrivate
        return this
    }

    fun build(): Review {
        return review
    }
}