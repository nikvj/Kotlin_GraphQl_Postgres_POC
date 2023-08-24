package com.graphQL.kotlin.resolvers

import com.graphQL.kotlin.model.Author
import com.graphQL.kotlin.model.Tutorial
import com.graphQL.kotlin.repository.AuthorRepository
import com.graphQL.kotlin.repository.TutorialRepository
import com.graphQL.kotlin.resolvers.mutations.Resolver
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*

class TutorialResolverTest {

    @Mock
    private lateinit var authorRepository: AuthorRepository

    private lateinit var tutorialResolver: TutorialResolver

    private lateinit var autoCloseable: AutoCloseable

    @BeforeEach
    fun setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this)
        tutorialResolver = TutorialResolver(authorRepository)
    }

    @AfterEach
    fun tearDown() {
        autoCloseable.close()
    }


    @Test
    fun getAuthor() {
        val author = Optional.of(Author(1,"Author", 22))
        val tutorial = Optional.of(Tutorial(1, "Title", "Description", author.get()))
        val authorId = tutorial.get().author?.id
        Mockito.`when`(authorId?.let { authorRepository.findById(it) }).thenReturn(author)

        val result = tutorialResolver.getAuthor(tutorial.get())

        assert(result == author.get())

    }
}