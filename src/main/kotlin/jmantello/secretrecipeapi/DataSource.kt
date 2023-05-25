package jmantello.secretrecipeapi


interface RecipeDataSource {
    fun getRecipes(): Collection<Recipe>
    fun getRecipeById(id: Int): Recipe
    fun createRecipe(recipe: Recipe): Recipe
    fun updateRecipe(recipe: Recipe): Recipe
    fun deleteRecipe(id: Int): Boolean
}

class DataSource : RecipeDataSource {
    override fun getRecipes(): Collection<Recipe> {
        TODO("Not yet implemented")
    }

    override fun getRecipeById(id: Int): Recipe {
        TODO("Not yet implemented")
    }

    override fun createRecipe(recipe: Recipe): Recipe {
        TODO("Not yet implemented")
    }

    override fun updateRecipe(recipe: Recipe): Recipe {
        TODO("Not yet implemented")
    }

    override fun deleteRecipe(id: Int): Boolean {
        TODO("Not yet implemented")
    }

}