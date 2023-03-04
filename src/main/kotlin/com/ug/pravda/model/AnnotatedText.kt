package com.ug.pravda.model

import jakarta.persistence.*

@Entity
class AnnotatedText (
        var content: String,
        var url: String,
        @OneToMany var annotations: MutableSet<Annotation> = mutableSetOf(),
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Int? = null,
)

data class AnnotatedTextMessage (
        val id: Int,
        val content: String,
        val url: String,
        val annotations: List<AnnotationStatsMessage>
)

data class CreateAnnotatedTextRequest (
        val content: String,
        val url: String
)