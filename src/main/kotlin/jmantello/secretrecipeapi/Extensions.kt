package jmantello.secretrecipeapi

import jmantello.secretrecipeapi.util.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

fun LocalDateTime.format(): String = this.format(englishDateFormatter)

private val daysLookup = (1..31).associate { it.toLong() to getOrdinal(it) }

private val englishDateFormatter = DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd")
    .appendLiteral(" ")
    .appendText(ChronoField.DAY_OF_MONTH, daysLookup)
    .appendLiteral(" ")
    .appendPattern("yyyy")
    .toFormatter(Locale.ENGLISH)

private fun getOrdinal(n: Int) = when {
    n in 11..13 -> "${n}th"
    n % 10 == 1 -> "${n}st"
    n % 10 == 2 -> "${n}nd"
    n % 10 == 3 -> "${n}rd"
    else -> "${n}th"
}

fun String.toSlug() = lowercase(Locale.getDefault())
    .replace("\n", " ")
    .replace("[^a-z\\d\\s]".toRegex(), " ")
    .split(" ")
    .joinToString("-")
    .replace("-+".toRegex(), "-")

class ResponseEntity {
    companion object {
        fun <T : Any> ok(result: T): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(HttpStatus.OK).body(ApiResponse(data = result))

        fun <T : Any> created(result: T): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(data = result))

        fun <T : Any> notFound(message: String): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse(error = message))

        fun <T : Any> badRequest(message: String): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse(error = message))

        fun <T : Any> unauthorized(message: String): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse(error = message))

        fun <T : Any> noContent(): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}