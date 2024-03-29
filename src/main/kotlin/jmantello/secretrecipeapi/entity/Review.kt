package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import jmantello.secretrecipeapi.entity.mapper.ReviewMapper
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.transfer.request.UpdateReviewRequest
import java.time.LocalDateTime

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

    fun update(request: UpdateReviewRequest) {
        request.title?.let { this.title = it }
        request.rating?.let { this.rating = it }
        request.content?.let { this.content = it }
        request.isPrivate?.let { this.isPrivate = it }
    }
}
