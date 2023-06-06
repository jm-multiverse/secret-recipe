package jmantello.secretrecipeapi.components.user

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserModel, Long> {
    fun findByEmail(email: String): UserModel?
}