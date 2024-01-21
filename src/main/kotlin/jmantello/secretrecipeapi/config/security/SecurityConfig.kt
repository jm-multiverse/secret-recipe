package jmantello.secretrecipeapi.config.security

import jmantello.secretrecipeapi.config.security.PublicEndpoints.PUBLIC_API_ENDPOINTS
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
        http {
            authorizeRequests {
                PUBLIC_API_ENDPOINTS.forEach { authorize(it.method, it.path, permitAll) }
                authorize(anyRequest, authenticated)
            }
            httpBasic { disable() } // If using JWT, may not need basic auth.
            csrf { disable() } // If building a stateless REST API, CSRF protection can be disabled.
            sessionManagement {
                sessionCreationPolicy =
                    SessionCreationPolicy.STATELESS // Ensure the application does not maintain session state
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
        }
        return http.build()
    }
}