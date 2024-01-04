package jmantello.secretrecipeapi.service

import io.jsonwebtoken.*
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.exception.InvalidJwtException
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(
    private val userService: UserService,
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.expirationMs}") private val jwtExpirationMs: Long,
    @Value("\${jwt.refreshExpirationDateInMs}") private val refreshExpirationDateInMs: Long
) {
    enum class TokenType(val tokenName: String) {
        ACCESS("accessToken"),
        REFRESH("refreshToken")
    }

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

    fun authenticate(token: String): Authentication {
        // 1. Parse the token to a valid user id
        // 2. Find the user by id
        // 3. Create an authentication object with the user and the user's authorities

        val claims = parseClaims(token)
        val userId = claims.subject.toLong()
        val user = userService.findByIdOrNull(userId)
            ?: throw InvalidJwtException("User associated with the token could not be found")

        val credentials = null // Credentials are null because we only store the user id in the token

        return UsernamePasswordAuthenticationToken(
            user,
            credentials,
            user.getGrantedAuthorities()
        )
    }

    private fun parseClaims(token: String): Claims {
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
