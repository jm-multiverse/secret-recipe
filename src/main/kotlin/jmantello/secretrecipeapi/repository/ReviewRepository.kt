package jmantello.secretrecipeapi.repository

import jmantello.secretrecipeapi.entity.Review
import org.springframework.data.repository.CrudRepository

interface ReviewRepository : CrudRepository<Review, Long> {
}