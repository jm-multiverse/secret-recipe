package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

@Entity
@Table(name="users")
class User {

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

    @JsonProperty("isAdmin")
    var isAdmin: Boolean = false

    @JsonProperty("isActive")
    var isActive: Boolean = true

    var dateCreated: String = LocalDateTime.now().toString()

    @OneToMany(mappedBy = "publisher")
    var publishedRecipes: MutableList<Recipe> = mutableListOf()

    @JsonManagedReference
    @ManyToMany
    @JoinTable(
        name = "user_saved_recipes",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "recipe_id")]
    )
    var savedRecipes: MutableList<Recipe> = mutableListOf()


    @OneToMany(mappedBy = "publisher")
    var publishedReviews: MutableList<Review> = mutableListOf()

    @ManyToMany(mappedBy = "followers")
    var following: MutableList<User> = mutableListOf()

    @ManyToMany
    @JoinTable(
        name = "user_followers",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "follower_id")]
    )
    var followers: MutableList<User> = mutableListOf()

    fun validatePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }

    fun getPublishedRecipes(limit: Int = publishedRecipes.size): List<Recipe> {
        return publishedRecipes.take(limit)
    }
}

// Builder
class UserBuilder {
    private val user = User()

    fun email(email: String): UserBuilder {
        user.email = email
        return this
    }

    fun password(password: String): UserBuilder {
        user.password = password
        return this
    }

    fun displayName(displayName: String): UserBuilder {
        user.displayName = displayName
        return this
    }

    fun isAdmin(isAdmin: Boolean): UserBuilder {
        user.isAdmin = isAdmin
        return this
    }

    fun isActive(isActive: Boolean): UserBuilder {
        user.isActive = isActive
        return this
    }

    fun build(): User {
        return user
    }
}

// DTOs
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
    var isAdmin: Boolean,
    var isActive: Boolean,
    var dateCreated: String,
)