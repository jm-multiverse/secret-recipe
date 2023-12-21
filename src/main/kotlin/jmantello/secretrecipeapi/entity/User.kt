package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jmantello.secretrecipeapi.dto.UpdateUserDTO
import jmantello.secretrecipeapi.entity.mapper.UserMapper
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

class UserDTO(
    val id: Long,
    val email: String,
    val displayName: String,
    val isAdmin: Boolean,
    val isActive: Boolean,
    val dateCreated: String,
    val publishedRecipes: List<RecipeDTO>,
    val savedRecipes: List<RecipeDTO>,
    val publishedReviews: List<ReviewDTO>,
    val followers: List<UserDTO>,
    val following: List<UserDTO>
    // TODO: Consider updating long id values to their corresponding entity
)

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

    var displayName: String? = ""

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

    fun getPublishedRecipes(limit: Int = publishedRecipes.size): List<Recipe> =
        publishedRecipes.take(limit)

    fun getSavedRecipes(limit: Int = savedRecipes.size): List<Recipe> =
         savedRecipes.take(limit)

    fun getPublishedReviews(limit: Int = publishedReviews.size): List<Review> =
        publishedReviews.take(limit)

    fun toDTO(): UserDTO = UserMapper.toDto(this)

    fun update(userDTO: UpdateUserDTO) {
        userDTO.email?.let { this.email = it }
        userDTO.password?.let { this.password = it }
        userDTO.displayName?.let { this.displayName = it }
        userDTO.isAdmin?.let { this.isAdmin = it }
        userDTO.isActive?.let { this.isActive = it }

    }
}