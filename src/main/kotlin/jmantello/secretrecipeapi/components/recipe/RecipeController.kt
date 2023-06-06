package jmantello.secretrecipeapi.components.recipe

import jmantello.secretrecipeapi.RecipeService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/recipe")
class RecipeController(private val service: RecipeService) {

    @GetMapping
    fun get(): Iterable<RecipeModel> = service.get()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): RecipeModel? = service.getById(id)

    @PostMapping
    fun create(@RequestBody recipe: RecipeModel): RecipeModel = service.create(recipe)

    @PutMapping
    fun update(@RequestBody recipe: RecipeModel): RecipeModel = service.update(recipe)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

}