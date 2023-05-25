package jmantello.secretrecipeapi

class RecipeService(private val ds: RecipeDataSource) {
    fun getRecipes(): Collection<Recipe> = ds.getRecipes()
    fun getRecipeById(id: Int): Recipe = ds.getRecipeById(id)
    fun createRecipe(recipe: Recipe): Recipe = ds.createRecipe(recipe)
    fun updateRecipe(recipe: Recipe): Recipe = ds.updateRecipe(recipe)
    fun deleteRecipe(id: Int): Boolean = ds.deleteRecipe(id)
}