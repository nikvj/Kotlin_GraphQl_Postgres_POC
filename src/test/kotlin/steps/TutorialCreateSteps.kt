package steps

import com.graphQL.kotlin.model.Author
import com.graphQL.kotlin.model.RequestModel.TutorialRequestModel
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

class TutorialCreateSteps: En {
    private val authorRepository = mockk<AuthorRepository>()
    private val tutorialRepository = mockk<TutorialRepository>()

    private lateinit var resolver: Resolver
    private lateinit var author: Author
    private lateinit var tutorial: Tutorial
    private lateinit var tutorialRequestModel: TutorialRequestModel
    private val slot = slot<Tutorial>()

    init{
        Given("a user wants to create a tutorial") {
            resolver = Resolver(authorRepository, tutorialRepository)
        }
        When(
            "they provide the tutorial details:"
        ) { dataTable: DataTable? ->
            val tutorialData = dataTable?.asMaps<String,String>(String::class.java, String::class.java)?.get(0)
            tutorialRequestModel = TutorialRequestModel(tutorialData?.get("title")!!,
                tutorialData["description"]!!,tutorialData["authorId"]!!.toInt())
        }

        When(
            "they provide corresponding author details:"
        ) { dataTable: DataTable? ->
            val authorData = dataTable?.asMaps<String,String>(String::class.java, String::class.java)?.get(0)
            author = Author(authorData?.get("id")?.toInt(), authorData?.get("name"), authorData?.get("age")?.toInt())
        }
        Then("the tutorial is created") {
            every { authorRepository.findById(tutorialRequestModel.authorId!!) } returns Optional.of(author)
            tutorial = Tutorial(null, tutorialRequestModel.title,
                tutorialRequestModel.description, author)
            every { tutorialRepository.save(any()) } returns tutorial

            val result = resolver.createTutorial(tutorialRequestModel)
            verify { tutorialRepository.save(capture(slot)) }
            expectThat(slot.captured).isEqualTo(tutorial)
            expectThat(result).isEqualTo(tutorial)
        }
    }
}