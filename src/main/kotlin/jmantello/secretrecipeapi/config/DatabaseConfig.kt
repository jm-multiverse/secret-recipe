package jmantello.secretrecipeapi.config

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.entity.User
import jmantello.secretrecipeapi.repository.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfig {

    // Database
    @Bean
    fun databaseInitializer(recipeRepository: RecipeRepository, userRepository: UserRepository) = ApplicationRunner {

        val friedEgg = recipeRepository.save(
            Recipe(
                title = "Fried Egg",
                content = "Heat oil or butter in non-stick pan on medium-high heat. Crack egg into the hot oil, let firm, then swirl in pan to set bottom. After the whites of the egg on top are mostly cooked, flip the egg over in the pan. Cook for another 30 seconds or so, until desired doneness achieved. Voila, your fried egg is ready.",
                author = 1,
                isPrivate = false,
                tags = mutableListOf("breakfast"),
                reviews = mutableListOf()
            )
        )

        val user = User()
        user.email = "email@example.com"
        user.password = "password"
        userRepository.save(user)
    }
}