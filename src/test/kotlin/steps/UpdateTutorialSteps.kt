package steps

import com.graphQL.kotlin.model.Author
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
import io.mockk.slot
import io.mockk.verify
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder.Options
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*

class UpdateTutorialSteps: En {
    private val authorRepository = mockk<AuthorRepository>()
    private val tutorialRepository = mockk<TutorialRepository>()

    private lateinit var resolver: Resolver
    private lateinit var tutorialUpdateRequestModel: TutorialUpdateRequestModel
    private lateinit var tutorial: Optional<Tutorial>
    private lateinit var author: Author
    private val slot = slot<Tutorial>()

    init {
        Given("a user wants to update a tutorial"){
            resolver = Resolver(authorRepository, tutorialRepository)
        }

        When("they provide the updated tutorial details:"){
            dataTable: DataTable? ->
            val tutorialUpdateData = dataTable?.asMaps<String,String>(String::class.java, String::class.java)?.get(0)
            tutorialUpdateRequestModel = TutorialUpdateRequestModel(tutorialUpdateData?.get("id")!!.toInt(),
                tutorialUpdateData["title"]!!,tutorialUpdateData["description"]!!,
                tutorialUpdateData["authorId"]!!.toInt())
        }

        When(
            "they provide corresponding author details for update tutorial:"
        ) { dataTable: DataTable? ->
            val authorData = dataTable?.asMaps<String,String>(String::class.java, String::class.java)?.get(0)
            author = Author(authorData?.get("id")?.toInt(), authorData?.get("name"), authorData?.get("age")?.toInt())
        }

        Then("the tutorial is updated"){
            tutorial = Optional.of(Tutorial(tutorialUpdateRequestModel.id, "tutorial", "desc", author))
            every { tutorialRepository.findById(tutorialUpdateRequestModel.id) } returns tutorial.map { it }
            every { authorRepository.findById(tutorialUpdateRequestModel.authorId) } returns Optional.of(author)
            val updatedTutorial = Tutorial(tutorialUpdateRequestModel.id, tutorialUpdateRequestModel.title,
                tutorialUpdateRequestModel.description, author)
            every { tutorialRepository.save(any()) } returns updatedTutorial

            val result = resolver.updateTutorial(tutorialUpdateRequestModel)
            verify { tutorialRepository.save(capture(slot)) }
            expectThat(slot.captured).isEqualTo(updatedTutorial)
            expectThat(result).isEqualTo(updatedTutorial)
        }
    }
}