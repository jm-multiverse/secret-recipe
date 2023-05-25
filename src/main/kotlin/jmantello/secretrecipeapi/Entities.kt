package jmantello.secretrecipeapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Recipe(
    var title: String,
    var content: String,
)