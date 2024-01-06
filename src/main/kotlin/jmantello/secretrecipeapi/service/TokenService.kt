package jmantello.secretrecipeapi.service

import io.jsonwebtoken.*
import jmantello.secretrecipeapi.exception.InvalidJwtException
import jmantello.secretrecipeapi.transfer.model.UserDTO
import jmantello.secretrecipeapi.util.Result
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
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
        REFRESH("refreshToken"),
    }

    fun generateAccessToken(userDTO: UserDTO): Result<String> {
        return try {
            val now = Date()
            val expiryDate = Date(now.time + jwtExpirationMs)

            val token = Jwts.builder()
                .setSubject(userDTO.id.toString())
                .claim("roles", userDTO.roles.map { it.name })
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()

            Success(token)
        } catch (e: Exception) {
            Error("Error generating access token: ${e.message}")
        }
    }

    fun generateRefreshToken(userDTO: UserDTO): Result<String> {
        return try {
            val now = Date()
            val expiryDate = Date(now.time + refreshExpirationDateInMs)

            val refreshToken = Jwts.builder()
                .setSubject(userDTO.id.toString())
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
        val userDTO = when (val result = userService.findById(userId)) {
            is Success -> result.data
            is Error -> throw InvalidJwtException("User associated with the token could not be found")
        }

        val principle = userDTO

        // Credentials are null because we only store the users id in the token
        val credentials = null

        // Currently, ROLE is a custom way to keep track of a user's roles.
        // Eventually, we'll want to use Spring Security to manage roles.
        // The "ROLE_" prefix is Spring Security's convention, so we'll keep it.
        val grantedAuthorities = userDTO.roles.map { SimpleGrantedAuthority("ROLE_${it.name}") }

        return UsernamePasswordAuthenticationToken(
            principle,
            credentials,
            grantedAuthorities
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
