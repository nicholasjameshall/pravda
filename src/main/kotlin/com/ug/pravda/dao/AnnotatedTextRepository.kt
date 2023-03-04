package com.ug.pravda.dao

import com.ug.pravda.model.AnnotatedText
import org.springframework.data.jpa.repository.JpaRepository

interface AnnotatedTextRepository : JpaRepository<AnnotatedText, Int> {
    fun findByUrl(url: String): List<AnnotatedText>
}