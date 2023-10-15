package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

@Entity
@Table(name="users")
class User {
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

    var isAdmin: Boolean = false
    var dateCreated: LocalDateTime = LocalDateTime.now()
    var displayName: String = ""
    var isActive: Boolean = true
    var publishedRecipes: MutableList<Long> = mutableListOf()
    var savedRecipes: MutableList<Long> = mutableListOf()
    var reviews: MutableList<Long> = mutableListOf()
    var followers: MutableList<Long> = mutableListOf()
    var following: MutableList<Long> = mutableListOf()

    fun validatePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}

class UserDTO(
    var email: String,
    var password: String
)