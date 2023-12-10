package jmantello.secretrecipeapi.util

import org.springframework.http.ResponseEntity

object ResponseBuilder {
    fun <T : Any> respond(result: Result<T>): ResponseEntity<ApiResponse<T>> {

        val apiResponse = when (result) {
            is Result.Success -> ApiResponse(data = result.data)
            is Result.Error -> ApiResponse(error = result.message)
        }

        return ResponseEntity.status(result.status).body(apiResponse)
    }
}