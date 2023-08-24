package com.graphQL.kotlin.resolvers.mutations

import com.graphQL.kotlin.model.Author
import com.graphQL.kotlin.model.RequestModel.AuthorRequestModel
import com.graphQL.kotlin.model.RequestModel.AuthorUpdateRequestModel
import com.graphQL.kotlin.model.RequestModel.TutorialRequestModel
import com.graphQL.kotlin.model.RequestModel.TutorialUpdateRequestModel
import com.graphQL.kotlin.model.Tutorial
import com.graphQL.kotlin.repository.AuthorRepository
import com.graphQL.kotlin.repository.TutorialRepository
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.*


@Controller
class Resolver(authorRepository: AuthorRepository,
               tutorialRepository: TutorialRepository
){

    lateinit var authorRepository: AuthorRepository
    lateinit var tutorialRepository: TutorialRepository

    init {
        this.authorRepository = authorRepository
        this.tutorialRepository = tutorialRepository
    }

    @MutationMapping
    fun createAuthor(@Argument authorInput: AuthorRequestModel): Author {
        val author = Author(name = authorInput.name, age = authorInput.age)
        authorRepository.save(author)
        return author
    }

    @MutationMapping
    fun createTutorial(@Argument tutorialInput: TutorialRequestModel): Tutorial {
        val author = tutorialInput.authorId?.let { authorRepository.findById(it).orElse(null) }
        val tutorial = Tutorial(title = tutorialInput.title, description = tutorialInput.description,
            author = author)
        tutorialRepository.save(tutorial)
        return tutorial
    }

    @MutationMapping
    fun deleteTutorial(@Argument id: Int): Boolean {
        tutorialRepository.deleteById(id)
        return true
    }

    @Throws(ChangeSetPersister.NotFoundException::class)
    @MutationMapping
    fun updateTutorial(@Argument tutorialUpdateInput: TutorialUpdateRequestModel): Tutorial {
        val optTutorial: Optional<Tutorial?> = tutorialRepository.findById(tutorialUpdateInput.id)
        if (optTutorial.isPresent) {
            val tutorial = optTutorial.get()
            tutorial.title = tutorialUpdateInput.title
            tutorial.description = tutorialUpdateInput.description
            tutorialRepository.save(tutorial)
            return tutorial
        }
        throw ChangeSetPersister.NotFoundException()
    }

    @Throws(ChangeSetPersister.NotFoundException::class)
    @MutationMapping
    fun updateAuthor(@Argument authorUpdateInput: AuthorUpdateRequestModel): Author {
        val optAuthor: Optional<Author?> = authorRepository.findById(authorUpdateInput.id)
        if (optAuthor.isPresent) {
            val author = optAuthor.get()
            author.name = authorUpdateInput.name
            author.age = authorUpdateInput.age
            authorRepository.save(author)
            return author
        }
        throw ChangeSetPersister.NotFoundException()
    }

    @QueryMapping
    fun findAllAuthors(): List<Author?> {
        return authorRepository.findAll()
    }

    @QueryMapping
    fun findAllTutorials(): List<Tutorial?> {
        return tutorialRepository.findAll()
    }
}
