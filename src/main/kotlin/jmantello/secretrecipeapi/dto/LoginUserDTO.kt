package jmantello.secretrecipeapi.dto

import jmantello.secretrecipeapi.annotations.ValidPassword
import javax.validation.constraints.Email

open class LoginUserDTO(
    @Email
    var email: String,

    @ValidPassword
    var password: String
)
