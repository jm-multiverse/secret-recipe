package jmantello.secretrecipeapi.util

import org.springframework.http.HttpStatus

sealed class Result<out T> {
    abstract val status: HttpStatus
    data class Success<T>(val data: T, override val status: HttpStatus = HttpStatus.OK) : Result<T>()
    data class Error(val message: String, override val status: HttpStatus = HttpStatus.BAD_REQUEST) : Result<Nothing>()
}