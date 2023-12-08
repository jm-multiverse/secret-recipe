package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityReference
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reviews")
class Review {
    @Id
    @GeneratedValue
    val id: Long = 0

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val datePublished: String = LocalDateTime.now().toString()

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    var publisher: User? = null

    var title: String = ""
    var rating: Double = 0.0
    @Lob var content: String = ""

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    var recipe: Recipe? = null

    @ManyToMany
    @JoinTable(
        name = "review_likes",
        joinColumns = [JoinColumn(name = "review_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var likes: MutableList<User> = mutableListOf()

    var isPrivate: Boolean = false
}

// Review Builder
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

    fun content(content: String): ReviewBuilder {
        review.content = content
        return this
    }

    fun rating(rating: Double): ReviewBuilder {
        review.rating = rating
        return this
    }

    fun build(): Review {
        return review
    }
}

class PublishReviewRequest(
    val publisherId: Long,
    val title: String,
    val content: String,
    val rating: Double,
)

class ReviewResponse(
    val id: Long,
    val publisherId: Long,
    val title: String,
    val content: String,
    val rating: Double,
    val datePublished: String,
    val likes: MutableList<Long>,
    val isPrivate: Boolean
)