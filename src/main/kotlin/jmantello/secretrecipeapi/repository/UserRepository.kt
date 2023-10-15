package jmantello.secretrecipeapi.repository

import jmantello.secretrecipeapi.entity.User
import org.springframework.data.repository.CrudRepository


interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): User?
}