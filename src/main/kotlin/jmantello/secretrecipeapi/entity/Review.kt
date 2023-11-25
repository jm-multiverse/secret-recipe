package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityReference
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Review {
    @Id
    @GeneratedValue
    val id: Long = 0

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val datePublished: LocalDateTime = LocalDateTime.now()

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    var publisher: User? = null

    var title: String = ""
    var rating: Double = 0.0
    @Lob var content: String = ""
    val likes: MutableList<Long> = mutableListOf()
    var isPrivate: Boolean = false
}

class PublishReviewRequest(
    val publisherId: Long,
    val title: String,
    val rating: Double,
    val content: String,
)