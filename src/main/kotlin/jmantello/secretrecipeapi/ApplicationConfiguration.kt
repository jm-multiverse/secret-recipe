package jmantello.secretrecipeapi

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
class ApplicationConfiguration {

    // Database
    @Bean
    fun databaseInitializer(recipeRepository: RecipeRepository) = ApplicationRunner {

        val friedEgg = recipeRepository.save(Recipe(title = "Fried Egg", content = "Heat oil or butter in non-stick pan on medium-high heat. Crack egg into the hot oil, let firm, then swirl in pan to set bottom. After the whites of the egg on top are mostly cooked, flip the egg over in the pan. Cook for another 30 seconds or so, until desired doneness achieved. Voila, your fried egg is complete."))
    }

    // Security
    @Bean
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        val apiPattern = "/api/**"
        http {
            securityMatcher(apiPattern)
            authorizeRequests {
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