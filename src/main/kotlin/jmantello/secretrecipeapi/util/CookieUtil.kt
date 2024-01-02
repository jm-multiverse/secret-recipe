package jmantello.secretrecipeapi.util

import jakarta.servlet.http.Cookie

object CookieUtil {
    fun create(name: String, value: String, maxAge: Int, httpOnly: Boolean = true, secure: Boolean = true): Cookie {
        val cookie = Cookie(name, value)
        cookie.maxAge = maxAge
        cookie.isHttpOnly = httpOnly
        cookie.secure = secure
        cookie.path = "/" // Set to root path
        // Set SameSite if needed
        return cookie
    }
}