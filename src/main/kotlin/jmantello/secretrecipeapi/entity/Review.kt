package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Lob
import java.time.LocalDateTime

@Entity
class Review(
    @Id
    @GeneratedValue
    val id: Long? = null,

    val author: Long,

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val datePublished: LocalDateTime = LocalDateTime.now(),

    var title: String,
    var rating: String,
    var content: String,
    val likes: MutableList<Long>,
    val isPrivate: Boolean,
)