package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Recipe() {
    @Id
    @GeneratedValue
    val id: Long = 0

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val datePublished: String = LocalDateTime.now().toString()

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    var publisher: User? = null
    var title: String = ""

    @Lob
    var content: String = ""
    val tags: MutableList<String> = mutableListOf()
    val reviews: List<Long> = mutableListOf()

    @JsonProperty("isPrivate")
    var isPrivate: Boolean = false
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
    val reviews: List<Long>,
    var isPrivate: Boolean,
)
