package jmantello.secretrecipeapi.service

import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.transfer.model.UserDTO
import jmantello.secretrecipeapi.util.Result.Success
import jmantello.secretrecipeapi.util.Result.Error
import org.springframework.stereotype.Service


@Service
class UserContext(private val authenticationService: AuthenticationService) {
    fun getCurrentUserEntity(): User {
        return when (val result = authenticationService.getCurrentUserEntity()) {
            is Success -> result.data
            is Error -> throw IllegalStateException(result.message)
        }
    }

    fun getCurrentUserId(): Long {
        return when (val result = authenticationService.getCurrentUserId()) {
            is Success -> result.data
            is Error -> throw IllegalStateException(result.message)
        }
    }

    fun getCurrentUserDTO(): UserDTO {
        return when (val result = authenticationService.getCurrentUserDTO()) {
            is Success -> result.data
            is Error -> throw IllegalStateException(result.message)
        }
    }
}