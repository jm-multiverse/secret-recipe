package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityReference
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
    val datePublished: LocalDateTime = LocalDateTime.now()

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    var publisher: User? = null

    var title: String = ""
    @Lob var content: String = ""
    val tags: MutableList<String> = mutableListOf()
    val reviews: MutableList<Long> = mutableListOf()
    var isPrivate: Boolean = false
}

class CreateRecipeRequest(
    val publisherId: Long,
    val title: String,
    val content: String,
)
