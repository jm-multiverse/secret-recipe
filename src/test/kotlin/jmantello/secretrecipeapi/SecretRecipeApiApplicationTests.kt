package jmantello.secretrecipeapi

//import jmantello.secretrecipeapi.entity.Recipe
//import jmantello.secretrecipeapi.entity.User
//import jmantello.secretrecipeapi.repository.RecipeRepository
//import jmantello.secretrecipeapi.repository.UserRepository
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.data.repository.findByIdOrNull
//
//@SpringBootTest
//class SecretRecipeApiApplicationTests {
//
//    @Test
//    fun contextLoads() {
//    }
//
//}
//
//@DataJpaTest
//class RepositoriesTests @Autowired constructor(
//    val entityManager: TestEntityManager,
//    val userRepository: UserRepository,
//    val recipeRepository: RecipeRepository,
//) {
//
//    @Test
//    fun `When findByIdOrNull then return Recipe`() {
//        val burger =
//            Recipe(
//                title = "Cheese Burger",
//                content = "Burger on grill, flip, add cheese, put on bun, and you're done.",
//                author = 1,
//                isPrivate = false,
//                tags = mutableListOf("breakfast"),
//                reviews = mutableListOf()
//            )
//
//        entityManager.persist(burger)
//        entityManager.flush()
//
//        val found = recipeRepository.findByIdOrNull(burger.id!!)
//        assertThat(found).isEqualTo(burger)
//    }
//
//    @Test
//    fun `When findByEmail then return User`() {
//        val user = User()
//        user.email = "test@example.com"
//        user.password = "password"
//        userRepository.save(user)
//        entityManager.persist(user)
//        entityManager.flush()
//
//        val found = userRepository.findByEmail(user.email)
//        assertThat(found).isEqualTo(user)
//    }
//
//    @Test
//    fun `Register new user`() {
//
//    }
//}
//
