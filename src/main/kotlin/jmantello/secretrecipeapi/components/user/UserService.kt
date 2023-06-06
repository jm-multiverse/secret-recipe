package jmantello.secretrecipeapi

import jmantello.secretrecipeapi.components.user.UserModel
import jmantello.secretrecipeapi.components.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(private val repository: UserRepository) {
    fun get(): Iterable<UserModel> = repository.findAll()
    fun getById(id: Long): UserModel? = repository.findByIdOrNull(id)
    fun create(recipe: UserModel): UserModel = repository.save(recipe)
    fun update(recipe: UserModel): UserModel = repository.save(recipe)
    fun delete(id: Long): Unit = repository.deleteById(id)
    fun login(user: UserModel): String {
        val user = repository.findByEmail(user.email)
        val passwordMatch = user?.validatePassword(user.password) ?: "Login failure"
        if (passwordMatch == true) return "Login success"
        return "Login failure"
    }

}