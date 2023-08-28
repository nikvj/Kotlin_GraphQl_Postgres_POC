package com.graphQL.kotlin.model

import jakarta.persistence.*

@Entity
class Tutorial (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int?=null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description")
    var description: String,

    @ManyToOne
    @JoinColumn(name = "author_id")
    var author: Author?
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tutorial

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (author != other.author) return false

        return true
    }
}
