package steps

import com.graphQL.kotlin.model.Author
import com.graphQL.kotlin.model.RequestModel.AuthorRequestModel
import com.graphQL.kotlin.model.RequestModel.AuthorUpdateRequestModel
import com.graphQL.kotlin.model.RequestModel.TutorialRequestModel
import com.graphQL.kotlin.model.RequestModel.TutorialUpdateRequestModel
import com.graphQL.kotlin.model.Tutorial
import com.graphQL.kotlin.repository.AuthorRepository
import com.graphQL.kotlin.repository.TutorialRepository
import com.graphQL.kotlin.resolvers.mutations.Resolver
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import io.mockk.every
import io.mockk.mockk
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CreateAuthorSteps : En {
    private val authorRepository = mockk<AuthorRepository>()
    private val tutorialRepository = mockk<TutorialRepository>()

    private lateinit var resolver: Resolver
    private lateinit var authorRequestModel: AuthorRequestModel
    private lateinit var author: Author
    init {
        Given("a user wants to create an author") {
            resolver = Resolver(authorRepository, tutorialRepository)
        }
        When(
            "they provide the author details:"
        ) { dataTable: DataTable? ->
            val authorData = dataTable?.asMaps<String,String>(String::class.java, String::class.java)?.get(0)
            authorRequestModel = AuthorRequestModel(authorData?.get("name")!!, authorData["age"]!!.toInt())
        }
        Then("the author is created") {
            author = Author(null, authorRequestModel.name, authorRequestModel.age)
            every { authorRepository.save(any()) } returns author

            val result = resolver.createAuthor(authorRequestModel)

            expectThat(result).isEqualTo(author)
        }
    }
}
