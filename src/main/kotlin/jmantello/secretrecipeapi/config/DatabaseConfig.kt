package jmantello.secretrecipeapi.config

import jmantello.secretrecipeapi.entity.builder.RecipeBuilder
import jmantello.secretrecipeapi.entity.builder.ReviewBuilder
import jmantello.secretrecipeapi.entity.builder.UserBuilder
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.repository.ReviewRepository
import jmantello.secretrecipeapi.repository.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfig {

    // Database
    @Bean
    fun databaseInitializer(
        recipeRepository: RecipeRepository,
        userRepository: UserRepository,
        reviewRepository: ReviewRepository
    ) = ApplicationRunner {

        // Create Users
        val admin = UserBuilder()
            .email("admin@email.com")
            .password("admin")
            .displayName("The Admin")
            .isAdmin(true)
            .build()

        val user1 = UserBuilder()
            .email("user1@email.com")
            .password("password")
            .displayName("John Smith")
            .build()

        val user2 = UserBuilder()
            .email("user2@email.com")
            .password("password")
            .displayName("Lucy Sandoval")
            .build()

        // Create Recipes
        val recipe = RecipeBuilder()
            .publisher(user1)
            .title("Egg Sandwich")
            .content("Put an egg between two slices of bread.")
            .tags(listOf("Snack"))
            .build()

        // Create Reviews
        val review = ReviewBuilder()
            .publisher(user2)
            .recipe(recipe)
            .title("Meh.")
            .content("Could have used more seasoning, like salt or pepper.")
            .rating(3.5)
            .build()

        // Save to repositories
        userRepository.save(admin)
        userRepository.save(user1)
        userRepository.save(user2)
        recipeRepository.save(recipe)
        reviewRepository.save(review)

    }
}