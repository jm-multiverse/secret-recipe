package jmantello.secretrecipeapi

import jakarta.persistence.Entity

@Entity
class Recipe(
    var title: String,
    var slug: String = title.toSlug(),
    var content: String,
)