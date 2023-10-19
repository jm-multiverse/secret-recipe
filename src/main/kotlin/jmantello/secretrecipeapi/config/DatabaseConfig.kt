package jmantello.secretrecipeapi.config

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.Review
import jmantello.secretrecipeapi.repository.RecipeRepository
import jmantello.secretrecipeapi.entity.User
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

        val admin = User()
        admin.isAdmin = true
        admin.email = "jmantello@email.com"
        admin.password = "password"
        admin.displayName = "The Admin"
        userRepository.save(admin)

        val user1 = User()
        user1.email = "user1@email.com"
        user1.password = "password"
        userRepository.save(user1)

        val user2 = User()
        user2.email = "user2@email.com"
        user2.password = "password"
        userRepository.save(user2)

        val recipe = Recipe()
        recipe.publisherId = user1.id
        recipe.title = "Egg Sandwich"
        recipe.content = "Put an egg between two slices of bread."
        recipe.tags.add("Snack")
        recipeRepository.save(recipe)

        val review = Review()
        review.publisherId = user2.id
        review.title = "Meh."
        review.content = "Could have used more seasoning, like salt or pepper."
        review.rating = 3.5
        reviewRepository.save(review)

    }
}