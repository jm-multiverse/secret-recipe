package jmantello.secretrecipeapi.util

import org.springframework.http.HttpStatus

sealed class Result<out T> {
    abstract val status: HttpStatus

    data class Success<T>(
        override val status: HttpStatus,
        val data: T,
    ) : Result<T>() {
        constructor(data: T) : this(HttpStatus.OK, data)
    }

    data class Error(
        override val status: HttpStatus,
        val message: String,
    ) : Result<Nothing>() {
        constructor(message: String) : this(HttpStatus.BAD_REQUEST, message)
    }
}