package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jmantello.secretrecipeapi.service.RecipeService
import jmantello.secretrecipeapi.service.ReviewService
import org.springframework.beans.factory.annotation.Autowired
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

    var password: String = ""
        @JsonIgnore
        get() = field
        set(value) {
            field = BCryptPasswordEncoder().encode(value)
        }

    var isActive: Boolean = true
    var isAdmin: Boolean = false
    var dateCreated: LocalDateTime = LocalDateTime.now()
    var displayName: String = ""

    @ManyToMany
    @JoinTable(
        name = "user_saved_recipes",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "recipe_id")]
    )
    val savedRecipes: MutableList<Recipe> = mutableListOf()

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