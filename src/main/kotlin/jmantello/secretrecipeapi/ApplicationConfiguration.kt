package jmantello.secretrecipeapi

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ApplicationConfiguration {

    // Database
    @Bean
    fun databaseInitializer(recipeRepository: RecipeRepository, userRepository: UserRepository) = ApplicationRunner {

        val friedEgg = recipeRepository.save(Recipe(title = "Fried Egg", content = "Heat oil or butter in non-stick pan on medium-high heat. Crack egg into the hot oil, let firm, then swirl in pan to set bottom. After the whites of the egg on top are mostly cooked, flip the egg over in the pan. Cook for another 30 seconds or so, until desired doneness achieved. Voila, your fried egg is ready."))

        val user = User()
        user.email = "email@example.com"
        user.password = "password"
        userRepository.save(user)
    }

    // Security
    @Bean
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        val apiPattern = "/api/**"
        http {
            securityMatcher(apiPattern)
            authorizeRequests {
                authorize(HttpMethod.POST,"/api/auth/register", permitAll)
                authorize(HttpMethod.POST,"/api/auth/login", permitAll)
                authorize(HttpMethod.GET,"/api/recipe", permitAll)
                authorize(HttpMethod.GET,"/api/recipe/*", permitAll)
                authorize(anyRequest, permitAll)
            }
            httpBasic { }

            // Csrf token disabled since api does not support users right now. Will be removed when users are added
            csrf {
                ignoringRequestMatchers(apiPattern)
            }
        }
        return http.build()
    }
}

@Configuration
@EnableWebMvc
class WebConfig: WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowCredentials(true)
    }
}