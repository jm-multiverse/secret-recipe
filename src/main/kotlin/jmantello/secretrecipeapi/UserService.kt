package jmantello.secretrecipeapi

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun emailRegistered(email: String): Boolean = userRepository.findByEmail(email) != null

    fun register(dto: UserDTO): ResponseEntity<User> {
        // If user with email exists, return bad request
        if(emailRegistered(dto.email))
            return ResponseEntity.badRequest().build()

        val user = User()
        user.email = dto.email
        user.password = dto.password
        return ResponseEntity.ok(userRepository.save(user))
    }

    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    fun findByIdOrNull(id: Int): User? = userRepository.findByIdOrNull(id)
}