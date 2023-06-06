package jmantello.secretrecipeapi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class Recipe(
    var title: String,
    @Column(length = 5000)
    var content: String,
    var addedAt: LocalDateTime = LocalDateTime.now(),
    @Id @GeneratedValue var id: Long? = null)

@Entity
class User(
    @Column(unique = true)
    var email: String,
    var hashPassword: String, // Look into BCrypt setter & validatePassword fun for User
    var salt: String,
    @Id @GeneratedValue var id: Long? = null)