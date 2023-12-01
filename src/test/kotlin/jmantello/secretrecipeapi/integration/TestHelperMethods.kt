package jmantello.secretrecipeapi.integration

import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class TestHelper {

    @Autowired
    private lateinit var userRepository: UserRepository

    fun getTestUser(): User {
        return userRepository.findByIdOrNull(2)
            ?: throw Error("Could not retrieve test user from db.")
    }
}