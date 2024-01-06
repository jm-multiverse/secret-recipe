package jmantello.secretrecipeapi.transfer.response

import jmantello.secretrecipeapi.transfer.model.UserDTO

class UserAuthenticatedResponse(
    val user: UserDTO,
    val accessToken: String,
    val refreshToken: String
)