package com.ug.pravda.dao

import com.ug.pravda.model.AnnotatedText
import com.ug.pravda.model.AnnotationStats
import com.ug.pravda.model.AnnotationType
import org.springframework.data.jpa.repository.JpaRepository

interface AnnotationStatsRepository: JpaRepository<AnnotationStats, Int> {
    fun findByAnnotatedText_Id(id: Int): List<AnnotationStats>

    fun findByAnnotatedTextAndType(text: AnnotatedText, type: AnnotationType): List<AnnotationStats>
}