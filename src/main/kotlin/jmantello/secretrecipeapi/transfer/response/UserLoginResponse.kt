package jmantello.secretrecipeapi.transfer.response

import jmantello.secretrecipeapi.entity.UserDTO

class UserLoginResponse(
    val user: UserDTO,
    val accessToken: String,
    val refreshToken: String
)