package jmantello.secretrecipeapi.entity.builder

import jmantello.secretrecipeapi.entity.Recipe
import jmantello.secretrecipeapi.entity.User

class RecipeBuilder {
    private val recipe = Recipe()

    fun publisher(publisher: User): RecipeBuilder {
        recipe.publisher = publisher
        return this
    }

    fun title(title: String): RecipeBuilder {
        recipe.title = title
        return this
    }

    fun content(content: String): RecipeBuilder {
        recipe.content = content
        return this
    }

    fun tags(tags: List<String>): RecipeBuilder {
        recipe.tags.addAll(tags)
        return this
    }

    fun build(): Recipe {
        return recipe
    }
}