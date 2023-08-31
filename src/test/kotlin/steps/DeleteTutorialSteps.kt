package steps

import com.graphQL.kotlin.repository.AuthorRepository
import com.graphQL.kotlin.repository.TutorialRepository
import com.graphQL.kotlin.resolvers.mutations.Resolver
import io.cucumber.java8.En
import io.mockk.*
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DeleteTutorialSteps: En {
    private val authorRepository = mockk<AuthorRepository>()
    private val tutorialRepository = mockk<TutorialRepository>()

    private lateinit var resolver: Resolver
    private var tutorialId: Int = 0
    private val slot = slot<Int>()

    init {
        Given("a user wants to delete a tutorial") {
            resolver = Resolver(authorRepository, tutorialRepository)
        }
        When(
            "they provide the tutorial ID {int}"
        ) { id: Int ->
            tutorialId = id

        }

        Then("the tutorial is deleted") {
            every { (tutorialRepository).deleteById(tutorialId) } just runs
            resolver.deleteTutorial(tutorialId)
            verify { (tutorialRepository).deleteById(capture(slot)) }
            expectThat(slot.captured).isEqualTo(tutorialId)
        }
    }
}