package jmantello.secretrecipeapi.exception

open class ResourceNotFoundException(message: String) : RuntimeException(message)
class UserNotFoundException(message: String) : ResourceNotFoundException(message)
class InvalidJwtException(message: String) : RuntimeException(message)