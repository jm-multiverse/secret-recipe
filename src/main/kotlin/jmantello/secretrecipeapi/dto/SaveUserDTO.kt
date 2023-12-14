package jmantello.secretrecipeapi.dto

import jmantello.secretrecipeapi.annotations.ValidPassword
import javax.validation.constraints.*

class SaveUserDTO(
    @Email
    val email: String?,

    @ValidPassword
    val password: String?,

    @Size(min = 1, max = 100)
    val displayName: String?,

    val isAdmin: Boolean?,
)