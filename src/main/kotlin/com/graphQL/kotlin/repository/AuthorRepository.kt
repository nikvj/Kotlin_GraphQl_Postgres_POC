package com.graphQL.kotlin.repository

import com.graphQL.kotlin.model.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository : JpaRepository<Author, Int>