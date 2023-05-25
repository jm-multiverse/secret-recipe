package jmantello.secretrecipeapi

import jakarta.persistence.Entity
import jmantello.secretrecipeapi.Extensions

@Entity
class Recipe(
    var title: String,
    var slug: String = title.toSlug(),
    var content: String,
)