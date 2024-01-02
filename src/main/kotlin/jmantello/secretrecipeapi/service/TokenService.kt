package jmantello.secretrecipeapi.service

import io.jsonwebtoken.*
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.exception.InvalidJwtException
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(
    private val userService: UserService,
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.expirationMs}") private val jwtExpirationMs: Long,
    @Value("\${jwt.refreshExpirationDateInMs}") private val refreshExpirationDateInMs: Long
) {
    fun generateAccessToken(user: User): Result<String> {
        return try {
            val now = Date()
            val expiryDate = Date(now.time + jwtExpirationMs)

            val token = Jwts.builder()
                .setSubject(user.id.toString())
                .claim("roles", user.roles.map { it.name })
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()

            Success(token)
        } catch (e: Exception) {
            Error("Error generating access token: ${e.message}")
        }
    }

    fun generateRefreshToken(user: User): Result<String> {
        return try {
            val now = Date()
            val expiryDate = Date(now.time + refreshExpirationDateInMs)

            val refreshToken = Jwts.builder()
                .setSubject(user.id.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()

            Success(refreshToken)
        } catch (e: Exception) {
            Error("Error generating refresh token: ${e.message}")
        }
    }

    fun validate(token: String): Result<User> {
        return try {
            val claims = parseToken(token)
            val userId = claims.subject.toLong()
            userService.findByIdOrNull(userId)?.let { Success(it) }
                ?: Error(UNAUTHORIZED, "User associated with the token could not be found")
        } catch (e: Exception) {
            Error(UNAUTHORIZED, "Invalid JWT token: ${e.message}")
        }
    }

    private fun parseToken(token: String): Claims {
        try {
            return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .body
        } catch (e: ExpiredJwtException) {
            throw InvalidJwtException("Expired JWT token")
        } catch (e: UnsupportedJwtException) {
            throw InvalidJwtException("Unsupported JWT token")
        } catch (e: MalformedJwtException) {
            throw InvalidJwtException("Malformed JWT token")
        } catch (e: SignatureException) {
            throw InvalidJwtException("Invalid JWT signature")
        } catch (e: IllegalArgumentException) {
            throw InvalidJwtException("Invalid JWT token")
        }
    }
}
