package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.*
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
    val publishedRecipes: List<Long>,
    val savedRecipes: List<Long>,
    val publishedReviews: List<Long>,
    val followers: List<Long>,
    val following: List<Long>
)

@Entity
@Table(name="users")
class User(
    @Id
    @GeneratedValue
    val id: Long = 0,

    @Column(unique = true)
    var email: String,

    @JsonIgnore
    var password: String,
    var displayName: String?,

    @JsonProperty("isAdmin")
    var isAdmin: Boolean,

    @JsonProperty("isActive")
    var isActive: Boolean = true,
    var dateCreated: String = LocalDateTime.now().toString(),

    @OneToMany(mappedBy = "publisher")
    var publishedRecipes: MutableList<Recipe> = mutableListOf(),

    @JsonManagedReference
    @ManyToMany
    @JoinTable(
        name = "user_saved_recipes",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "recipe_id")]
    )
    var savedRecipes: MutableList<Recipe> = mutableListOf(),

    @JsonManagedReference
    @OneToMany(mappedBy = "publisher")
    var publishedReviews: MutableList<Review> = mutableListOf(),

    @JsonManagedReference
    @ManyToMany
    @JoinTable(
        name = "review_likes",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "review_id")]
    )
    var likedReviews: MutableList<Review> = mutableListOf(),

    @JsonManagedReference
    @ManyToMany
    @JoinTable(
        name = "user_followers",
        joinColumns = [JoinColumn(name = "follower_id")],
        inverseJoinColumns = [JoinColumn(name = "followed_id")]
    )
    var following: MutableList<User> = mutableListOf(),

    @JsonBackReference
    @ManyToMany(mappedBy = "following")
    var followers: MutableList<User> = mutableListOf()
) {

    init {
        this.password = BCryptPasswordEncoder().encode(password)
    }

    fun toDTO(): UserDTO = UserMapper.toDto(this)

    fun validatePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }

    fun getPublishedRecipes(limit: Int = publishedRecipes.size): List<Recipe> =
        publishedRecipes.take(limit)

    fun getSavedRecipes(limit: Int = savedRecipes.size): List<Recipe> =
         savedRecipes.take(limit)

    fun getPublishedReviews(limit: Int = publishedReviews.size): List<Review> =
        publishedReviews.take(limit)

    fun update(userDTO: UpdateUserDTO) {
        userDTO.email?.let { this.email = it }
        userDTO.password?.let { this.password = it }
        userDTO.displayName?.let { this.displayName = it }
        userDTO.isAdmin?.let { this.isAdmin = it }
        userDTO.isActive?.let { this.isActive = it }
    }

    fun follow(user: User) {
        if(this == user) return
        if(this.following.contains(user)) return
        this.following.add(user)
    }

    fun unfollow(user: User) {
        if(this == user) return
        if(!this.following.contains(user)) return
        this.following.remove(user)
    }

    fun likeReview(review: Review) {
        if(this.likedReviews.contains(review)) return
        this.likedReviews.add(review)
    }

}