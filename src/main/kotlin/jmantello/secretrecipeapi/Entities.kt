package jmantello.secretrecipeapi

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

@Entity
class Recipe(
    var title: String,
    @Lob // Large Object - Likely to be longer than 256 characters
    var content: String,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val addedAt: LocalDateTime = LocalDateTime.now(),
    @Id @GeneratedValue val id: Long? = null)

@Entity
@Table(name="users")
class User { // Could I make this into a data class?
    @Id
    @GeneratedValue
    val id: Long? = null

    @Column(unique = true)
    var email: String = ""

    @Column
    var password: String = ""
        @JsonIgnore
        get() = field
        set(value) {
            field = BCryptPasswordEncoder().encode(value)
        }

    fun validatePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}

class UserDTO(
    var email: String,
    var password: String
)