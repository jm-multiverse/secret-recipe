package jmantello.secretrecipeapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Recipe(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long,
    var title: String,
    var slug: String = title.toSlug(),
    var content: String,
)