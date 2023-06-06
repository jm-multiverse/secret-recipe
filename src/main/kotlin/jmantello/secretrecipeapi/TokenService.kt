package jmantello.secretrecipeapi

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService {
    fun issue(userId: String): String {
        val jwt = Jwts.builder()
            .setIssuer(userId)
            .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // 1 day
            .signWith(SignatureAlgorithm.HS512, "secret-key")
            .compact()

        return jwt
    }
}