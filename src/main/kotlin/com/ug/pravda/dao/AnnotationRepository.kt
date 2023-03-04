package com.ug.pravda.dao

import com.ug.pravda.model.*
import com.ug.pravda.model.Annotation
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact
import org.springframework.data.jpa.repository.JpaRepository

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

interface AnnotationRepository : JpaRepository<Annotation, Int> {
    fun findBySubmitterAndAnnotatedTextAndType(
        submitter: User, annotatedText: AnnotatedText, annotationType: AnnotationType
    ): List<Annotation>

    fun findByAnnotatedText(annotatedText: AnnotatedText): List<Annotation>
}