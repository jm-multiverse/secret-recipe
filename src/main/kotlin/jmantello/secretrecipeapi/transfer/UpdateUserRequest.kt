package jmantello.secretrecipeapi.transfer

import jakarta.annotation.Nullable
import jmantello.secretrecipeapi.annotations.ValidPassword
import javax.validation.constraints.*

class UpdateUserRequest(
    @NotNull
    val id: Long?,

    @Email
    val email: String? = null,

    @ValidPassword
    val password: String? = null,

    @Nullable
    @Size(min = 1, max = 100)
    val displayName: String? = null,

    @Nullable
    val isAdmin: Boolean? = null,

    @Nullable
    val isActive: Boolean? = null
)