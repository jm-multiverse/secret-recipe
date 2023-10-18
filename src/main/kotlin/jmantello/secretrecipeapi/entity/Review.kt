package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Lob
import java.time.LocalDateTime

@Entity
class Review {
    @Id
    @GeneratedValue
    val id: Long? = null

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val datePublished: LocalDateTime = LocalDateTime.now()

    var publisher: Long? = null
    var title: String = ""
    var rating: Double = 0.0
    @Lob var content: String = ""
    val likes: MutableList<Long> = mutableListOf()
    var isPrivate: Boolean = false
}
