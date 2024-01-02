package jmantello.secretrecipeapi.transfer.response

import jmantello.secretrecipeapi.transfer.model.UserDTO

class UserLoginResponse(
    val user: UserDTO,
    val accessToken: String,
    val refreshToken: String
)