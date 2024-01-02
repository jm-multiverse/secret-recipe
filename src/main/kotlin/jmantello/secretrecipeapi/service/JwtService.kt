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
class JwtService(
    private val userService: UserService,
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.expirationMs}") private val jwtExpirationMs: Long,
    @Value("\${jwt.refreshExpirationDateInMs}") private val refreshExpirationDateInMs: Long

) {
    fun issueWithRoles(userId: Long, roles: List<String>): Result<String> {
        return try {
            val now = Date()
            val expiryDate = Date(now.time + jwtExpirationMs)

            val token = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()

            Success(token)
        } catch (e: Exception) {
            Error("Error generating JWT: ${e.message}")
        }
    }

    fun validate(jwt: String): Result<User> {
        return try {
            val claims = parseToken(jwt)
            val userId = claims.subject.toLong()
            userService.findByIdOrNull(userId)?.let { Success(it) }
                ?: Error(UNAUTHORIZED, "User associated with the token could not be found")
        } catch (e: Exception) {
            Error(UNAUTHORIZED, "Invalid JWT token: ${e.message}")
        }
    }

    private fun parseToken(jwt: String): Claims {
        try {
            return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(jwt)
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

    fun generateRefreshToken(userId: Long): Result<String> {
        return try {
            val now = Date()
            val expiryDate = Date(now.time + refreshExpirationDateInMs)

            val refreshToken = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()

            Success(refreshToken)
        } catch (e: Exception) {
            Error("Error generating refresh token: ${e.message}")
        }
    }
}
