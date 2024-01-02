package jmantello.secretrecipeapi.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import jmantello.secretrecipeapi.entity.User.Role.ADMIN
import jmantello.secretrecipeapi.entity.User.Role.USER
import jmantello.secretrecipeapi.entity.User.Status.ACTIVE
import jmantello.secretrecipeapi.entity.User.Status.SOFT_DELETED
import jmantello.secretrecipeapi.entity.filters.ActiveUsersFilter
import jmantello.secretrecipeapi.entity.mapper.UserMapper
import jmantello.secretrecipeapi.transfer.request.UpdateUserRequest
import org.hibernate.annotations.Filter
import org.hibernate.annotations.FilterDef
import org.hibernate.annotations.Filters
import org.hibernate.annotations.ParamDef
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

class UserDTO(
    val id: Long,
    val email: String,
    val displayName: String,
    val dateCreated: String,
    val publishedRecipes: List<Long>,
    val savedRecipes: List<Long>,
    val publishedReviews: List<Long>,
    val followers: List<Long>,
    val following: List<Long>,
    val roles: List<User.Role>,
    val status: User.Status,
)

@Entity
@Table(name = "users")
@FilterDef(name = ActiveUsersFilter.NAME, parameters = [ParamDef(name = ActiveUsersFilter.PARAM, type = String::class)])
@Filters(Filter(name = ActiveUsersFilter.NAME, condition = ActiveUsersFilter.CONDITION))
class User(
    @Id
    @GeneratedValue
    val id: Long = 0,

    @Column(unique = true)
    var email: String,

    @JsonIgnore
    var password: String,

    var displayName: String = "",

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
    var followers: MutableList<User> = mutableListOf(),

    var roles: MutableList<Role> = mutableListOf(USER),

    var status: Status = ACTIVE,
) {

    enum class Role {
        ADMIN,
        USER,
    }

    enum class Status {
        ACTIVE,
        INACTIVE,
        SOFT_DELETED,
    }

    init {
        this.password = BCryptPasswordEncoder().encode(password)
    }

    fun validatePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }

    fun isAdmin(): Boolean = this.roles.contains(ADMIN)
    fun isActive(): Boolean = this.status == ACTIVE
    fun isSoftDeleted(): Boolean = this.status == SOFT_DELETED

    fun getPublishedRecipes(limit: Int = publishedRecipes.size): List<Recipe> =
        publishedRecipes.take(limit)

    fun getSavedRecipes(limit: Int = savedRecipes.size): List<Recipe> =
        savedRecipes.take(limit)

    fun getPublishedReviews(limit: Int = publishedReviews.size): List<Review> =
        publishedReviews.take(limit)

    fun follow(user: User) {
        if (this == user) return
        if (this.following.contains(user)) return
        this.following.add(user)
    }

    fun unfollow(user: User) {
        if (this == user) return
        if (!this.following.contains(user)) return
        this.following.remove(user)
    }

    fun likeReview(review: Review) {
        if (this.likedReviews.contains(review)) return
        this.likedReviews.add(review)
    }

    fun update(userDTO: UpdateUserRequest) {
        userDTO.email?.let { this.email = it }
        userDTO.password?.let { this.password = it }
        userDTO.displayName?.let { this.displayName = it }
    }

    fun toDTO(): UserDTO = UserMapper.toDto(this)
}