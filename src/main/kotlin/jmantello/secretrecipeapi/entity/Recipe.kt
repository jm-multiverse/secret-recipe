package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jmantello.secretrecipeapi.entity.mapper.RecipeMapper
import jmantello.secretrecipeapi.entity.mapper.UserMapper
import java.time.LocalDateTime

class RecipeDTO(
    val id: Long,
    val title: String,
    val content: String,
    val publisherId: Long,
    val datePublished: String,
    val reviews: List<Long>,
    val tags: List<String>,
    val rating: Double?,
    var isPrivate: Boolean,
)

@Entity
@Table(name = "recipes")
class Recipe {
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

    @Lob
    var content: String = ""

    @OneToMany(mappedBy = "recipe")
    var reviews: MutableList<Review> = mutableListOf()

    @JsonProperty("isPrivate")
    var isPrivate: Boolean = false

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
        return totalRating / reviews.size
    }

    fun getSaves(limit: Int = this.saves.size): List<User> {
        return this.saves.take(limit)
    }

    fun toDTO(): RecipeDTO = RecipeMapper.toDto(this)
}
