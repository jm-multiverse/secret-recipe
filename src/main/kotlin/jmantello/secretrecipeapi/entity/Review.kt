package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import jmantello.secretrecipeapi.entity.mapper.ReviewMapper
import java.time.LocalDateTime

class ReviewDTO(
    val id: Long,
    val title: String,
    val content: String?,
    val publisherId: Long,
    val recipeId: Long,
    val datePublished: String,
    val likes: List<Long>?,
    val rating: Double?,
    val isPrivate: Boolean
)

@Entity
@Table(name = "reviews")
class Review(
    @Id
    @GeneratedValue
    val id: Long = 0,

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val datePublished: String = LocalDateTime.now().toString(),

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    var publisher: User? = null,

    var title: String = "",

    var rating: Double = 0.0,

    @Lob var content: String? = "",

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    var recipe: Recipe? = null,

    @JsonBackReference
    @ManyToMany
    @JoinTable(
        name = "review_likes",
        joinColumns = [JoinColumn(name = "review_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var likes: MutableList<User> = mutableListOf(),

    var isPrivate: Boolean = false,
) {

    fun toDTO(): ReviewDTO = ReviewMapper.toDto(this)
}
