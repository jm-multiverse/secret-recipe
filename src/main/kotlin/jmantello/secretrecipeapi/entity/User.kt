package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

@Entity
@Table(name="users")
class User() {

    @Id
    @GeneratedValue
    val id: Long = 0

    @Column(unique = true)
    var email: String = ""

    @JsonIgnore
    var password: String = ""
        set(value) {
            field = BCryptPasswordEncoder().encode(value)
        }

    var displayName: String = ""

    @JsonProperty("isActive")
    var isActive: Boolean = true

    @JsonProperty("isAdmin")
    var isAdmin: Boolean = false

    var dateCreated: String = LocalDateTime.now().toString()

    @ManyToMany
    @JoinTable(
        name = "user_saved_recipes",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "recipe_id")]
    )
    var savedRecipes: MutableList<Recipe> = mutableListOf()

    var reviews: MutableList<Long> = mutableListOf()
    var followers: MutableList<Long> = mutableListOf()
    var following: MutableList<Long> = mutableListOf()

    fun validatePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}

class RegisterUserDTO(
    var email: String,
    var password: String,
    var displayName: String,
)

class LoginUserDTO(
    var email: String,
    var password: String
)

class UserResponseDTO(
    var id: Long,
    var email: String,
    var displayName: String,
    var isActive: Boolean,
    var isAdmin: Boolean,
    var dateCreated: String,
)