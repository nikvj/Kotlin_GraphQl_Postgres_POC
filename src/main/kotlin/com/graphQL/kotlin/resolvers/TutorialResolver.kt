package com.graphQL.kotlin.resolvers

import com.graphQL.kotlin.model.Author
import com.graphQL.kotlin.model.Tutorial
import com.graphQL.kotlin.repository.AuthorRepository
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Component


@Component
class TutorialResolver(authorRepository: AuthorRepository){

    lateinit var authorRepository: AuthorRepository

    init {
        this.authorRepository = authorRepository
    }

    @QueryMapping
    fun getAuthor(tutorial: Tutorial): Author? {
        return tutorial.author?.id?.let { authorRepository.findById(it).orElseThrow() }
    }
}
