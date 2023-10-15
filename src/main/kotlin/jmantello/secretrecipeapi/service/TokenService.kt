package jmantello.secretrecipeapi.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jmantello.secretrecipeapi.entity.User
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(private val userService: UserService) {
    fun issue(userId: String): String {
        val jwt = Jwts.builder()
            .setIssuer(userId)
            .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // 1 day
            .signWith(SignatureAlgorithm.HS512, "secret-key")
            .compact()

        return jwt
    }

    fun parse(jwt: String): User? {
        return try {
            val body = Jwts.parser().setSigningKey("secret-key").parseClaimsJws(jwt).body
            val userId = body.issuer.toLong()
            userService.findByIdOrNull(userId)
        } catch (e: Exception) {
            null
        }
    }
}