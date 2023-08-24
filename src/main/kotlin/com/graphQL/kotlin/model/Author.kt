package com.graphQL.kotlin.model

import jakarta.persistence.*

@Entity
data class Author (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @Column(name = "name", nullable = false)
    var name: String?,

    @Column(name = "age")
    var age: Int?
)
