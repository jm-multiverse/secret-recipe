package jmantello.secretrecipeapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SecretRecipeApiApplication

fun main(args: Array<String>) {
	runApplication<SecretRecipeApiApplication>(*args)
}
