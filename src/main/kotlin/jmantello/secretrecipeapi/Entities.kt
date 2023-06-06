package jmantello.secretrecipeapi

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

@Entity
class Recipe(
    var title: String,
    @Column(length = 5000)
    var content: String,
    var addedAt: LocalDateTime = LocalDateTime.now(),
    @Id @GeneratedValue var id: Long? = null)

@Entity
@Table(name="users")
class User { // Could I make this into a data class?
    @Id
    @GeneratedValue
    var id: Long? = null

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