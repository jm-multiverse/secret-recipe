package jmantello.secretrecipeapi.dto

import jmantello.secretrecipeapi.entity.UserDTO

class UserLoginResponse(
    val user: UserDTO,
    val accessToken: String,
    val refreshToken: String
)