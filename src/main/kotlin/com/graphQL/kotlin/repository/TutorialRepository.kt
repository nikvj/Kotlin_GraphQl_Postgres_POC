package com.graphQL.kotlin.repository

import com.graphQL.kotlin.model.Tutorial
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TutorialRepository : JpaRepository<Tutorial?, Int?>

