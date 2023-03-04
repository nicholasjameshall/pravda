package com.ug.pravda.model

import jakarta.persistence.*

@Entity
class AnnotationStats (
    @ManyToOne var annotatedText: AnnotatedText,
    var type: AnnotationType,
    var votes: Int,
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
)

data class AnnotationStatsMessage (
    val textId: Int,
    val type: AnnotationType,
    val votes: Int
)

fun AnnotationStats.toMessage(): AnnotationStatsMessage {
    return AnnotationStatsMessage(
        textId = this.annotatedText.id ?: throw Exception(),
        type = this.type,
        votes = this.votes
    )
}