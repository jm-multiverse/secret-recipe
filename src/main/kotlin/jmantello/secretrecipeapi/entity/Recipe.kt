package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Lob
import java.time.LocalDateTime

class Recipe(
    @Id
    @GeneratedValue
    val id: Long? = null,

    val author: Long,

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val datePublished: LocalDateTime = LocalDateTime.now(),

    var title: String,

    @Lob // Large Object - Likely to be longer than 256 characters
    var content: String,

    val isPrivate: Boolean,
    val tags: MutableList<String>,
    val reviews: MutableList<String>,
)