package jmantello.secretrecipeapi

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun emailRegistered(email: String): Boolean = userRepository.findByEmail(email) != null

    fun register(dto: UserDTO): ResponseEntity<String> {
        // If user with email exists, return bad request
        if(emailRegistered(dto.email))
            return ResponseEntity.badRequest().build()

        val user = User()
        user.email = dto.email
        user.password = dto.password
        userRepository.save(user)
        return ResponseEntity.ok("Successfully registered")
    }

    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    fun findByIdOrNull(id: Long): User? = userRepository.findByIdOrNull(id)

    fun login(dto: UserDTO): ResponseEntity<String> {
        val user = findByEmail(dto.email)
            ?: return ResponseEntity.badRequest().body("Invalid credentials")

        if(!user.validatePassword(dto.password))
            return ResponseEntity.badRequest().body("Invalid credentials")

        // Create token here

        return ResponseEntity.ok("Successfully logged in")
    }
}