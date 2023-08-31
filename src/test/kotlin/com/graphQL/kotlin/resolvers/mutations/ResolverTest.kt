import com.graphQL.kotlin.model.Author
import com.graphQL.kotlin.model.RequestModel.AuthorRequestModel
import com.graphQL.kotlin.model.RequestModel.AuthorUpdateRequestModel
import com.graphQL.kotlin.model.RequestModel.TutorialRequestModel
import com.graphQL.kotlin.model.RequestModel.TutorialUpdateRequestModel
import com.graphQL.kotlin.model.Tutorial
import com.graphQL.kotlin.repository.AuthorRepository
import com.graphQL.kotlin.repository.TutorialRepository
import com.graphQL.kotlin.resolvers.mutations.Resolver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.data.crossstore.ChangeSetPersister
import java.util.*

class ResolverTest {

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @Mock
    private lateinit var tutorialRepository: TutorialRepository

    private lateinit var resolver: Resolver

    private lateinit var autoCloseable: AutoCloseable

    @BeforeEach
    fun setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this)
        resolver = Resolver(authorRepository, tutorialRepository)
    }

    @AfterEach
    fun tearDown() {
       autoCloseable.close()
    }

    @Test
    fun `test createAuthor`() {
        val authorInput = AuthorRequestModel("John Doe", 30)
        val author = Author(null, "John Doe", 30)
        Mockito.`when`(authorRepository.save(ArgumentMatchers.any(Author::class.java))).thenReturn(author)

        resolver.createAuthor(authorInput)
        val authorArgumentCapture: ArgumentCaptor<Author> =
            ArgumentCaptor.forClass(Author::class.java)

        Mockito.verify(authorRepository).save(authorArgumentCapture.capture())
        val authorCaptured : Author = authorArgumentCapture.value
        assertThat(authorCaptured).isEqualTo(author)
    }

    @Test
    fun `test createTutorial`() {
        val tutorialInput = TutorialRequestModel("Title", "Description", 1)
        val author = Author(1, "Author1", 30)
        val tutorial = Tutorial(null, "Title", "Description", author)

        Mockito.`when`(authorRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Optional.of(author))
        Mockito.`when`(tutorialRepository.save(ArgumentMatchers.any(Tutorial::class.java))).thenReturn(tutorial)

        resolver.createTutorial(tutorialInput)
        val tutorialArgumentCapture: ArgumentCaptor<Tutorial> =
            ArgumentCaptor.forClass(Tutorial::class.java)

        Mockito.verify(tutorialRepository).save(tutorialArgumentCapture.capture())
        val tutorialCaptured : Tutorial = tutorialArgumentCapture.value
        assertThat(tutorialCaptured).isEqualTo(tutorial)
    }

    @Test
    fun `test updateTutorial when tutorial exists`() {
        val tutorialId = 1
        val tutorialUpdateInput = TutorialUpdateRequestModel(tutorialId, "Updated Title", "Updated Description", authorId = 1)
        val author = Author(1, "Author1", 30)
        val existingTutorial = Optional.of(Tutorial(tutorialId, "Title", "Description", author))
        Mockito.`when`(tutorialRepository.findById(tutorialId)).thenReturn(existingTutorial.map { it })
        Mockito.`when`(tutorialRepository.save(ArgumentMatchers.any(Tutorial::class.java))).thenAnswer { it.arguments[0] }

        resolver.updateTutorial(tutorialUpdateInput)
        val tutorialArgumentCapture: ArgumentCaptor<Tutorial> =
            ArgumentCaptor.forClass(Tutorial::class.java)

        Mockito.verify(tutorialRepository).save(tutorialArgumentCapture.capture())
        val tutorialCaptured : Tutorial = tutorialArgumentCapture.value
        assertThat(tutorialCaptured.id).isEqualTo(tutorialId)
        assertThat(tutorialCaptured.title).isEqualTo(tutorialUpdateInput.title)
        assertThat(tutorialCaptured.description).isEqualTo(tutorialUpdateInput.description)
    }

    @Test
    fun `test updateTutorial when tutorial does not exist`() {
        val tutorialId = 1
        val tutorialUpdateInput = TutorialUpdateRequestModel(tutorialId, "Updated Title", "Updated Description", authorId = 1)
        Mockito.`when`(tutorialRepository.findById(tutorialId)).thenReturn(Optional.empty<Tutorial>().map { it })

        assertThrows<ChangeSetPersister.NotFoundException> {
            resolver.updateTutorial(tutorialUpdateInput)
        }
    }

    @Test
    fun `test deleteTutorial`() {
        val id = 1
        Mockito.doNothing().`when`(tutorialRepository).deleteById(id)

        resolver.deleteTutorial(id)
        val tutorialArgumentCapture: ArgumentCaptor<Int> =
            ArgumentCaptor.forClass(Int::class.java)

        Mockito.verify(tutorialRepository).deleteById(tutorialArgumentCapture.capture())
        val tutorialCaptured : Int = tutorialArgumentCapture.value
        assertThat(tutorialCaptured).isEqualTo(id)
    }

    @Test
    fun `test update author when author exists`() {
        val authorUpdateRequestModel = AuthorUpdateRequestModel(1,
            "Author",
            30)

        val updatedAuthor = Author(1, "Author1", 29)
        Mockito.`when`(authorRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Optional.of(updatedAuthor))
        Mockito.`when`(authorRepository.save(ArgumentMatchers.any(Author::class.java))).thenReturn(updatedAuthor)

        resolver.updateAuthor(authorUpdateRequestModel)
        val authorArgumentCapture: ArgumentCaptor<Author> =
            ArgumentCaptor.forClass(Author::class.java)

        Mockito.verify(authorRepository).save(authorArgumentCapture.capture())
        val authorCaptured : Author = authorArgumentCapture.value
        assertThat(authorCaptured.id).isEqualTo(authorUpdateRequestModel.id)
        assertThat(authorCaptured.name).isEqualTo(authorUpdateRequestModel.name)
        assertThat(authorCaptured.age).isEqualTo(authorUpdateRequestModel.age)
    }

    @Test
    fun `test update author when author does not exists`() {
        val authorUpdateRequestModel = AuthorUpdateRequestModel(1,
            "Author",
            30)

        Mockito.`when`(authorRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Optional.empty())

        assertThrows<ChangeSetPersister.NotFoundException> {
            resolver.updateAuthor(authorUpdateRequestModel)
        }
    }

    @Test
    fun `test getAllAuthors`() {
        val authorList: List<Author> = listOf(Author(1, "Author1", 30),
            Author(2, "Author2", 29))
        Mockito.`when`(authorRepository.findAll()).thenReturn(authorList)

        val result = resolver.findAllAuthors()
        assert(result == authorList)
    }

    @Test
    fun `test getAllTutorials`() {
        val author = Author(1, "Author1", 30)
        val tutorialList: List<Tutorial> = listOf(Tutorial(1,
            "title1",
            "desc1",author),
            Tutorial(2,
                "title2",
                "desc2",author))
        Mockito.`when`(tutorialRepository.findAll()).thenReturn(tutorialList)

        val result = resolver.findAllTutorials()
        assert(result == tutorialList)
    }

}
