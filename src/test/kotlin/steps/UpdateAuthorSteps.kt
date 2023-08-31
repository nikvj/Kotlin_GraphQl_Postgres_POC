package steps

import com.graphQL.kotlin.model.Author
import com.graphQL.kotlin.model.RequestModel.AuthorUpdateRequestModel
import com.graphQL.kotlin.model.Tutorial
import com.graphQL.kotlin.repository.AuthorRepository
import com.graphQL.kotlin.repository.TutorialRepository
import com.graphQL.kotlin.resolvers.mutations.Resolver
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*

class UpdateAuthorSteps: En {
    private val authorRepository = mockk<AuthorRepository>()
    private val tutorialRepository = mockk<TutorialRepository>()

    private lateinit var resolver: Resolver
    private lateinit var author: Author
    private lateinit var authorUpdateRequestModel: AuthorUpdateRequestModel
    private val slot = slot<Author>()

    init {
        Given("a user wants to update an author") {
            resolver = Resolver(authorRepository, tutorialRepository)
        }
        When(
            "they provide the updated author details:"
        ) { dataTable: DataTable? ->
            val authorData = dataTable?.asMaps<String,String>(String::class.java, String::class.java)?.get(0)
            authorUpdateRequestModel = AuthorUpdateRequestModel(authorData?.get("id")!!.toInt(),
                authorData["name"]!!,authorData["age"]!!.toInt())
        }

        Then("the author is updated") {
            author = Author(authorUpdateRequestModel.id, "Author", 29)
            every { authorRepository.findById(authorUpdateRequestModel.id) } returns Optional.of(author)
            val updatedAuthor = Author(authorUpdateRequestModel.id, authorUpdateRequestModel.name,
                authorUpdateRequestModel.age)
            every { authorRepository.save(any()) } returns updatedAuthor

            val result = resolver.updateAuthor(authorUpdateRequestModel)
            verify { authorRepository.save(capture(slot)) }
            expectThat(slot.captured).isEqualTo(updatedAuthor)
            expectThat(result).isEqualTo(updatedAuthor)
        }
    }
}