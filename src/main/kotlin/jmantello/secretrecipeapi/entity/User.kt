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
import jmantello.secretrecipeapi.transfer.model.RecipeDTO
import jmantello.secretrecipeapi.transfer.model.ReviewDTO
import jmantello.secretrecipeapi.transfer.model.UserDTO
import jmantello.secretrecipeapi.transfer.request.UpdateUserRequest
import org.hibernate.annotations.Filter
import org.hibernate.annotations.FilterDef
import org.hibernate.annotations.Filters
import org.hibernate.annotations.ParamDef
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@FilterDef(name = ActiveUsersFilter.NAME, parameters = [ParamDef(name = ActiveUsersFilter.PARAMETER_NAME, type = String::class)])
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

    // Currently, ROLE is a custom way to keep track of a user's roles.
    // Eventually, we'll want to use Spring Security to manage roles.
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

    fun update(userDTO: UpdateUserRequest) {
        userDTO.email?.let { this.email = it }
        userDTO.password?.let { this.password = it }
        userDTO.displayName?.let { this.displayName = it }
    }

    fun isAdmin(): Boolean = this.roles.contains(ADMIN)
    fun isActive(): Boolean = this.status == ACTIVE
    fun isSoftDeleted(): Boolean = this.status == SOFT_DELETED

    fun getPublishedRecipes(limit: Int = publishedRecipes.size): List<RecipeDTO> =
        publishedRecipes.take(limit).map { it.toDTO() }

    fun getSavedRecipes(limit: Int = savedRecipes.size): List<RecipeDTO> =
        savedRecipes.take(limit).map { it.toDTO() }

    fun saveRecipe(recipe: Recipe) {
        if (this.savedRecipes.contains(recipe)) return
        this.savedRecipes.add(recipe)
    }

    fun unsaveRecipe(recipe: Recipe) {
        if (!this.savedRecipes.contains(recipe)) return
        this.savedRecipes.remove(recipe)
    }

    fun getPublishedReviews(limit: Int = publishedReviews.size): List<ReviewDTO> =
        publishedReviews.take(limit).map { it.toDTO() }

    fun getLikedReviews(limit: Int = likedReviews.size): List<ReviewDTO> =
        likedReviews.take(limit).map { it.toDTO() }

    fun likeReview(review: Review) {
        if (this.likedReviews.contains(review)) return
        this.likedReviews.add(review)
    }

    fun unlikeReview(review: Review) {
        if (!this.likedReviews.contains(review)) return
        this.likedReviews.remove(review)
    }

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

    fun getUserFollowers(): List<UserDTO> {
        return this.followers.map { it.toDTO() }
    }

    fun getUserFollowing(): List<UserDTO> {
        return this.following.map { it.toDTO() }
    }

    fun toDTO(): UserDTO = UserMapper.toDto(this)
}