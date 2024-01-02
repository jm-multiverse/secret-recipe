package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jmantello.secretrecipeapi.entity.mapper.RecipeMapper
import jmantello.secretrecipeapi.transfer.model.RecipeDTO
import jmantello.secretrecipeapi.transfer.request.UpdateRecipeRequest
import java.time.LocalDateTime

@Entity
@Table(name = "recipes")
class Recipe(
    @Id
    @GeneratedValue
    val id: Long = 0,

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val datePublished: String = LocalDateTime.now().toString(),

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    var publisher: User? = null,

    var title: String,

    @Lob
    var content: String = "",

    @OneToMany(mappedBy = "recipe")
    var reviews: MutableList<Review> = mutableListOf(),

    @JsonProperty("isPrivate")
    var isPrivate: Boolean = false,

    @ManyToMany
    @JoinTable(
        name = "recipe_saves",
        joinColumns = [JoinColumn(name = "recipe_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var saves: MutableList<User> = mutableListOf(),

    val tags: MutableList<String> = mutableListOf(),
) {

    fun calculateOverallRating(): Double? {
        if (reviews.isEmpty()) return null

        val totalRating = reviews.sumOf { it.rating }
        return totalRating / reviews.size
    }

    fun getSaves(limit: Int = this.saves.size): List<User> {
        return this.saves.take(limit)
    }

    fun toDTO(): RecipeDTO = RecipeMapper.toDto(this)

    fun update(recipe: UpdateRecipeRequest) {
        recipe.publisherId // TODO: Implement publisher update
        recipe.title?.let { title = it }
        recipe.content?.let { content = it }
        recipe.isPrivate?.let { isPrivate = it }
        recipe.tags?.let { tags.addAll(it) }
    }
}
