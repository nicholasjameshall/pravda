package com.ug.pravda.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.GenerationType
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne

enum class AnnotationType {
    INACCURATE,
    BIAS,
    NO_SOURCE
}

enum class Sentiment {
    POSITIVE,
    NEGATIVE
}

@Entity
class Annotation (
    var type: AnnotationType,
    var sentiment: Sentiment,
    @ManyToOne var annotatedText: AnnotatedText,
    @OneToOne var submitter: User,
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Int? = null,
)

data class AnnotationMessage (
    val type: AnnotationType,
    val sentiment: Sentiment,
    val annotatedTextId: Int,
    val submitterId: Int,
    val id: Int
)

fun Annotation.toMessage(): AnnotationMessage {
    return AnnotationMessage(
        id = this.id ?: throw Exception("Annotation has no ID"),
        type = this.type,
        sentiment = this.sentiment,
        annotatedTextId = this.annotatedText.id ?: throw Exception("Annotated text has no ID"),
        submitterId = this.submitter.id ?: throw Exception("Submitter has no ID")
    )
}

data class CreateAnnotationRequest (
    val type: AnnotationType,
    val sentiment: Sentiment,
    val textId: Int,
    val userId: Int
)