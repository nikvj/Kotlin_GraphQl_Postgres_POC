package com.graphQL.kotlin.IntegrationTest

import com.fasterxml.jackson.databind.ObjectMapper
import com.graphQL.kotlin.model.Author
import com.graphQL.kotlin.model.RequestModel.AuthorRequestModel
import com.graphQL.kotlin.model.RequestModel.AuthorUpdateRequestModel
import com.graphQL.kotlin.model.RequestModel.TutorialRequestModel
import com.graphQL.kotlin.model.RequestModel.TutorialUpdateRequestModel
import com.graphQL.kotlin.model.Tutorial
import com.graphQL.kotlin.repository.AuthorRepository
import com.graphQL.kotlin.repository.TutorialRepository
import com.graphQL.kotlin.resolvers.mutations.Resolver
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(Resolver::class)
class ResolverIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var authorRepository: AuthorRepository

    @MockBean
    private lateinit var tutorialRepository: TutorialRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `test createAuthor mutation`() {
        val savedAuthor = Author(id = 1, name = "Author", age = 30)

        mockMvc.perform(
            post("/graphql")
                .content(
                    objectMapper.writeValueAsString(
                        "mutation { createAuthor(authorInput: " +
                                "{ name: \"Author\", age: 30 }) " +
                                "{ id name age } }"
                    )
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect {
                status().isOk
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.data.createAuthor.id").value(savedAuthor.id)
                jsonPath("$.data.createAuthor.name").value(savedAuthor.name)
                jsonPath("$.data.createAuthor.age").value(savedAuthor.age)
            }
    }

    @Test
    fun `test createTutorial mutation`() {
        val author = Author(id = 1, name = "Author", age = 30)
        val savedTutorial = Tutorial(id = 1, title = "title", description = "desc", author)

        mockMvc.perform(
            post("/graphql")
                .content(
                    objectMapper.writeValueAsString(
                        "mutation{ createTutorial(tutorialInput: " +
                                "{ title: \"title\" description:\"desc\" authorId: 1 })" +
                                "{ id title description author{ id } } }"
                    )
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect {
                status().isOk
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.data.createTutorial.id").value(savedTutorial.id)
                jsonPath("$.data.createTutorial.title").value(savedTutorial.title)
                jsonPath("$.data.createTutorial.desc").value(savedTutorial.description)
                jsonPath("$.data.createTutorial.author.id").value(savedTutorial.author?.id)

            }
    }

    @Test
    fun `test deleteTutorial mutation`() {
        val tutorialId = 1

        mockMvc.perform(
            delete("/graphql")
                .content(objectMapper.writeValueAsString("mutation{ deleteTutorial( id: $tutorialId ) }"))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect {
                status().isOk
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.data.deleteTutorial").value(true)
            }
    }

    @Test
    fun `test updateTutorial mutation`() {
        val author = Author(id = 1, name = "Author", age = 30)
        val updatedTutorial = Tutorial(id = 1, title = "title", description = "desc", author)

        mockMvc.perform(
            post("/graphql")
                .content(objectMapper.writeValueAsString("mutation{ updateTutorial(tutorialUpdateInput: " +
                        "{ id: 1 title: \"title\" description: \"desc\" })" +
                        "{ id title description author{id name age} } }"))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect{
                status().isOk
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.data.tutorialUpdateInput.id").value(updatedTutorial.id)
                jsonPath("$.data.tutorialUpdateInput.title").value(updatedTutorial.title)
                jsonPath("$.data.tutorialUpdateInput.desc").value(updatedTutorial.description)
                jsonPath("$.data.tutorialUpdateInput.author.id").value(updatedTutorial.author?.id)
                jsonPath("$.data.tutorialUpdateInput.author.name").value(updatedTutorial.author?.name)
                jsonPath("$.data.tutorialUpdateInput.author.age").value(updatedTutorial.author?.age)
            }
    }

    @Test
    fun `test updateAuthor mutation`() {
        val updatedAuthor = Author(id = 1, name = "Author", age = 30)
        mockMvc.perform(
            post("/graphql")
                .content(objectMapper.writeValueAsString("mutation{ updateAuthor(authorUpdateInput:" +
                        " { id: 1 name: \"Author\" age: 30 }){ id name age } }"))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect{
                status().isOk
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.data.authorUpdateInput.id").value(updatedAuthor.id)
                jsonPath("$.data.authorUpdateInput.title").value(updatedAuthor.name)
                jsonPath("$.data.authorUpdateInput.desc").value(updatedAuthor.age)
            }
    }

    @Test
    fun `test findAllAuthors query`() {
        val authors: List<Author> = listOf(Author(id = 1, name = "Author1", age = 30),
            Author(id = 2, name = "Author2", age = 31))
        mockMvc.perform(
            post("/graphql")
                .content(objectMapper.writeValueAsString("query{ findAllAuthors{ id name age } }"))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect{
                status().isOk
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.data.findAllAuthors.id").value(authors[0].id)
                jsonPath("$.data.findAllAuthors.title").value(authors[0].name)
                jsonPath("$.data.findAllAuthors.desc").value(authors[0].age)
            }
    }

    @Test
    fun `test findAllTutorials query`() {
        val author = Author(id = 1, name = "Author1", age = 30)
        val tutorial: List<Tutorial> = listOf(Tutorial(
            id = 1, title = "title1", description = "desc1", author),
            Tutorial(
                id = 1, title = "title2", description = "desc2", author))
        mockMvc.perform(
            post("/graphql")
                .content(objectMapper.writeValueAsString("query{ findAllTutorial{" +
                        " id title description author { id }} }"))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect{
                status().isOk
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.data.findAllAuthors.id").value(tutorial[0].id)
                jsonPath("$.data.findAllAuthors.title").value(tutorial[0].title)
                jsonPath("$.data.findAllAuthors.desc").value(tutorial[0].description)
            }
    }
}
