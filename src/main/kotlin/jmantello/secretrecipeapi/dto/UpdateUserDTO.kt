package jmantello.secretrecipeapi.dto

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.entity.User
import javax.validation.constraints.*

class UpdateUserDTO(
    @Email
    val email: String?,

    // TODO: Add custom validation for password strength
    @Size(min = 8, max = 100)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$") // At least one lowercase, one uppercase, and one digit
    @Pattern(regexp = "^(?=.*[!@#\$%^&*()\\-_=+\\\\|\\[\\]{};:\\'\\\",<.>/?]).*$") // At least one special character
    @Pattern(regexp = "^(?!.*\\s).*$") // No whitespace
    val password: String?,

    @Size(min = 1, max = 100)
    val displayName: String?,

    val isAdmin: Boolean?,
)