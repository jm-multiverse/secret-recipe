package jmantello.secretrecipeapi.dto

import jakarta.annotation.Nullable
import jmantello.secretrecipeapi.annotations.ValidPassword
import javax.validation.constraints.*

class SaveUserDTO(
    val id: Long?,

    @Email
    val email: String?,

    @ValidPassword
    val password: String?,

    @Nullable
    @Size(min = 1, max = 100)
    val displayName: String?,

    @Nullable
    val isAdmin: Boolean?,

    @Nullable
    val isActive: Boolean?
)