package jmantello.secretrecipeapi.util

import jakarta.servlet.http.Cookie

object Cookie {
    fun create(name: String, value: String, maxAge: Int, httpOnly: Boolean = true, secure: Boolean = true): Cookie {
        val cookie = Cookie(name, value)
        cookie.maxAge = maxAge
        cookie.isHttpOnly = httpOnly
        cookie.secure = secure
        cookie.path = "/"
        // Set SameSite if needed
        return cookie
    }

    fun createClear(name: String): Cookie {
        val cookie = Cookie(name, "")
        cookie.maxAge = 0
        cookie.isHttpOnly = true
        cookie.secure = true
        cookie.path = "/"
        return cookie
    }
}