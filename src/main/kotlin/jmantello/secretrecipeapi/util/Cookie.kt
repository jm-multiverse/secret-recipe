package jmantello.secretrecipeapi.util

import jakarta.servlet.http.Cookie
import jmantello.secretrecipeapi.service.TokenService.TokenType.ACCESS
import jmantello.secretrecipeapi.service.TokenService.TokenType.REFRESH

object Cookie {
    const val accessTokenExpiryDuration = 3600 // seconds, 1 hour
    const val refreshTokenExpiryDuration = 604800 // seconds, 1 week

    fun create(name: String, value: String, maxAge: Int, httpOnly: Boolean = true, secure: Boolean = true): Cookie {
        val cookie = Cookie(name, value)
        cookie.maxAge = maxAge
        cookie.isHttpOnly = httpOnly
        cookie.secure = secure
        cookie.path = "/"
        // Set SameSite if needed
        return cookie
    }

    fun createStandardAccessCookie(value: String): Cookie {
        return create(ACCESS.tokenName, value, accessTokenExpiryDuration)
    }

    fun createStandardRefreshCookie(value: String): Cookie {
        return create(REFRESH.tokenName, value, refreshTokenExpiryDuration)
    }

    fun createClear(name: String): Cookie {
        return create(
            name = name,
            value = "",
            maxAge = 0,
            httpOnly = true,
        )
    }
}