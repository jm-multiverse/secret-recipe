package jmantello.secretrecipeapi.config

import jmantello.secretrecipeapi.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        val apiPattern = "/api/**"
        http {
            securityMatcher(apiPattern)
            authorizeRequests {
                authorize(HttpMethod.POST, "/api/auth/register", permitAll)
                authorize(HttpMethod.POST, "/api/auth/login", permitAll)
                authorize(anyRequest, authenticated)
            }
            httpBasic { disable() } // If using JWT, may not need basic auth.
            csrf { disable() } // If building a stateless REST API, CSRF protection can be disabled.
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS // Ensure the application does not maintain session state
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
        }
        return http.build()
    }
}