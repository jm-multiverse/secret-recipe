package jmantello.secretrecipeapi.entity.builder

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.User
import java.time.LocalDateTime

class RecipeBuilder {
    private var datePublished: String? = null
    private var publisher: User? = null
    private var title: String? = null
    private var content: String? = null
    private var reviews: MutableList<Review>? = null
    private var isPrivate: Boolean? = null
    private var saves: MutableList<User>? = null
    private var tags: MutableList<String>? = null

    fun datePublished(datePublished: String): RecipeBuilder {
        this.datePublished = datePublished
        return this
    }

    fun publisher(publisher: User): RecipeBuilder {
        this.publisher = publisher
        return this
    }

    fun title(title: String): RecipeBuilder {
        this.title = title
        return this
    }

    fun content(content: String): RecipeBuilder {
        this.content = content
        return this
    }

    fun reviews(reviews: MutableList<Review>): RecipeBuilder {
        this.reviews = reviews
        return this
    }

    fun isPrivate(isPrivate: Boolean): RecipeBuilder {
        this.isPrivate = isPrivate
        return this
    }

    fun saves(saves: MutableList<User>): RecipeBuilder {
        this.saves = saves
        return this
    }

    fun tags(tags: List<String>): RecipeBuilder {
        this.tags = tags.toMutableList()
        return this
    }

    fun build(): Recipe {
        return Recipe(
            datePublished = datePublished ?: LocalDateTime.now().toString(),
            publisher = publisher ?: throw Exception("Publisher cannot be null"),
            title = title ?: throw Exception("Title cannot be null"),
            content = content ?: "",
            reviews = reviews ?: mutableListOf(),
            isPrivate = isPrivate ?: false,
            saves = saves ?: mutableListOf(),
            tags = tags ?: mutableListOf()
        )
    }
}