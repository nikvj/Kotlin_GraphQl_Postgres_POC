package steps

import com.graphQL.kotlin.model.Author
import com.graphQL.kotlin.repository.AuthorRepository
import com.graphQL.kotlin.repository.TutorialRepository
import com.graphQL.kotlin.resolvers.mutations.Resolver
import io.cucumber.java8.En
import io.mockk.*
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class GetAllAuthorSteps: En {
    private val authorRepository = mockk<AuthorRepository>()
    private val tutorialRepository = mockk<TutorialRepository>()

    private lateinit var resolver: Resolver

    init {
        Given("a user wants to find all authors") {
            resolver = Resolver(authorRepository, tutorialRepository)
        }

        Then("a list of authors is returned") {
            val authorList: List<Author> = mutableListOf(Author(
                1, "author1", 29
            ), Author(2, "author2", 30)
            )
            every { authorRepository.findAll() } returns authorList
            val result = resolver.findAllAuthors()
            verify { authorRepository.findAll() }
            expectThat(result[0]).isEqualTo(authorList[0])
            expectThat(result[1]).isEqualTo(authorList[1])
        }
    }
}