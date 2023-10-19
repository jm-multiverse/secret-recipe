package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Lob
import java.time.LocalDateTime

@Entity
class Recipe() {
    @Id
    @GeneratedValue
    val id: Long? = null

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val datePublished: LocalDateTime = LocalDateTime.now()

    var publisherId: Long? = null

    var title: String = ""
    @Lob var content: String = ""
    val tags: MutableList<String> = mutableListOf()
    val reviews: MutableList<Long> = mutableListOf()
    var isPrivate: Boolean = false
}
