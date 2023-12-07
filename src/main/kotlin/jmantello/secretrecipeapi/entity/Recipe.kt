package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "recipes")
class Recipe() {
    @Id
    @GeneratedValue
    val id: Long = 0

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val datePublished: String = LocalDateTime.now().toString()

    @ManyToOne
    @JoinColumn(name = "user_id")
    var publisher: User? = null

    var title: String = ""

    @Lob
    var content: String = ""

    @OneToMany(mappedBy = "recipe")
    var reviews: MutableList<Review> = mutableListOf()

    @JsonProperty("isPrivate")
    var isPrivate: Boolean = false

    @Transient
    var overallRating: Double? = null

    @ManyToMany
    @JoinTable(
        name = "recipe_saves",
        joinColumns = [JoinColumn(name = "recipe_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var saves: MutableList<User> = mutableListOf()

    val tags: MutableList<String> = mutableListOf()

    fun calculateOverallRating(): Double? {
        if (reviews.isEmpty()) return null

        val totalRating = reviews.sumOf { it.rating }
        return totalRating.toDouble() / reviews.size
    }
}

class CreateRecipeRequest(
    val publisherId: Long,
    val title: String,
    val content: String,
)

class RecipeResponse(
    val id: Long,
    val title: String,
    val content: String,
    val datePublished: String,
    val publisherId: Long,
    val tags: List<String>,
    val reviews: List<Review>,
    var isPrivate: Boolean,
)
