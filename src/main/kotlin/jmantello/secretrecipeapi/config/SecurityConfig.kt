package jmantello.secretrecipeapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain


@Configuration
class SecurityConfig {

    @Bean
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        val apiPattern = "/api/**"
        http {
            securityMatcher(apiPattern)
            authorizeRequests {
                authorize(HttpMethod.POST,"/api/auth/register", permitAll)
                authorize(HttpMethod.POST,"/api/auth/login", permitAll)
                authorize(anyRequest, permitAll) // authenticated
            }
            httpBasic { }
            oauth2ResourceServer {
                jwt {  }
            }

            // Csrf token disabled since api does not support users right now. Will be removed when users are added
            csrf {
                ignoringRequestMatchers(apiPattern)
            }
        }
        return http.build()
    }
}