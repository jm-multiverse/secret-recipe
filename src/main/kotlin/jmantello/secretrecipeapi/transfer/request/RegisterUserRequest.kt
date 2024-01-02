package jmantello.secretrecipeapi.transfer.request

import jakarta.annotation.Nullable
import jmantello.secretrecipeapi.annotations.ValidPassword
import javax.validation.constraints.*

class RegisterUserRequest(
    @Email
    val email: String,

    @ValidPassword
    val password: String,

    @Nullable
    @Size(min = 1, max = 100)
    val displayName: String = "",

    @Nullable
    val isAdmin: Boolean = false,
)