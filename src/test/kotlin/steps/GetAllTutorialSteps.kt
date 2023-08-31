package steps

import com.graphQL.kotlin.model.Author
import com.graphQL.kotlin.model.Tutorial
import com.graphQL.kotlin.repository.AuthorRepository
import com.graphQL.kotlin.repository.TutorialRepository
import com.graphQL.kotlin.resolvers.mutations.Resolver
import io.cucumber.java8.En
import io.mockk.MockK
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class GetAllTutorialSteps: En {
    private val authorRepository = mockk<AuthorRepository>()
    private val tutorialRepository = mockk<TutorialRepository>()

    private lateinit var resolver: Resolver

    init {
        Given("a user wants to find all tutorials") {
            resolver = Resolver(authorRepository, tutorialRepository)
        }

        Then("a list of tutorials is returned") {
            val author = Author(1, "author1", 29)
            val tutorialList: List<Tutorial> = mutableListOf(
                Tutorial(1, "title1", "desc1", author),
                Tutorial(2, "title2", "desc2", author)
            )
            every { tutorialRepository.findAll() } returns tutorialList
            val result = resolver.findAllTutorials()
            verify { tutorialRepository.findAll() }
            expectThat(result[0]).isEqualTo(tutorialList[0])
            expectThat(result[1]).isEqualTo(tutorialList[1])
        }
    }
}