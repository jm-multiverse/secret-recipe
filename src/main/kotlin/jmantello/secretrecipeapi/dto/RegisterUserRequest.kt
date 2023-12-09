package jmantello.secretrecipeapi.dto

class RegisterUserRequest(
    var email: String,
    var password: String,
    var displayName: String,
)