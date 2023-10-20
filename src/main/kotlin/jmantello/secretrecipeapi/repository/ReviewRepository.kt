package jmantello.secretrecipeapi.repository

import jmantello.secretrecipeapi.entity.Review
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ReviewRepository : CrudRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.publisher.id = :publisherId")
    fun findAllByPublisherId(publisherId: Long?): MutableList<Review>
}