package jmantello.secretrecipeapi.transfer.request

import jmantello.secretrecipeapi.annotations.ValidPassword
import javax.validation.constraints.Email

class UserLoginRequest(
    @Email
    var email: String,

    @ValidPassword
    var password: String
)
