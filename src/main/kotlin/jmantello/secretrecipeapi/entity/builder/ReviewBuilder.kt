package jmantello.secretrecipeapi.entity.builder

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.transfer.request.UpdateReviewRequest
import java.time.LocalDateTime

class ReviewBuilder {
    private var datePublished: String? = null
    private var publisher: User? = null
    private var title: String? = null
    private var rating: Double? = null
    private var content: String? = null
    private var recipe: Recipe? = null
    private var likes: MutableList<User>? = null
    private var isPrivate: Boolean? = null

    fun datePublished(datePublished: String): ReviewBuilder {
        this.datePublished = datePublished
        return this
    }

    fun publisher(publisher: User): ReviewBuilder {
        this.publisher = publisher
        return this
    }

    fun title(title: String): ReviewBuilder {
        this.title = title
        return this
    }

    fun rating(rating: Double): ReviewBuilder {
        this.rating = rating
        return this
    }

    fun content(content: String?): ReviewBuilder {
        this.content = content
        return this
    }

    fun isPrivate(isPrivate: Boolean): ReviewBuilder {
        this.isPrivate = isPrivate
        return this
    }

    fun recipe(recipe: Recipe): ReviewBuilder {
        this.recipe = recipe
        return this
    }

    fun build(): Review {
        return Review(
            datePublished = this.datePublished?: LocalDateTime.now().toString(),
            publisher = this.publisher?: throw Exception("ReviewBuilder: Publisher cannot be null when building a review"),
            title = this.title?: throw Exception("ReviewBuilder: Title cannot be null when building a review"),
            rating = this.rating?: throw Exception("ReviewBuilder: Rating cannot be null when building a review"),
            content = this.content?: "",
            recipe = this.recipe?: throw Exception("ReviewBuilder: Recipe cannot be null when building a review"),
            likes = this.likes?: mutableListOf(),
            isPrivate = this.isPrivate?: false
        )
    }
}