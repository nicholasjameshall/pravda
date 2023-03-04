package com.ug.pravda.controller

import com.ug.pravda.assembler.AnnotatedTextSummaryModelAssembler
import com.ug.pravda.dao.AnnotatedTextRepository
import com.ug.pravda.dao.AnnotationRepository
import com.ug.pravda.dao.AnnotationStatsRepository
import com.ug.pravda.exception.AnnotatedTextsNotFoundException
import com.ug.pravda.exception.AnnotationNotFoundException
import com.ug.pravda.model.AnnotatedText
import com.ug.pravda.model.AnnotatedTextMessage
import com.ug.pravda.model.CreateAnnotatedTextRequest
import com.ug.pravda.model.toMessage
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RestController
class AnnotatedTextController(
    private val annotatedTextRepository: AnnotatedTextRepository,
    private val annotationStatsRepository: AnnotationStatsRepository,
    private val annotationRepository: AnnotationRepository,
    private val annotatedTextSummaryModelAssembler: AnnotatedTextSummaryModelAssembler
) {
    @PostMapping(path = ["/annotatedtexts"])
    fun createAnnotatedText(@RequestBody request: CreateAnnotatedTextRequest) : ResponseEntity<EntityModel<AnnotatedTextMessage>> {
        // TODO: verify URL
        val newAnnotatedText = AnnotatedText(
            content = request.content,
            url = request.url
        )

        val createdAnnotatedText: AnnotatedText = annotatedTextRepository.save(newAnnotatedText)

        val annotatedTextMessage = AnnotatedTextMessage(
            // TODO: Make this field no longer optional!
            id = createdAnnotatedText.id ?: throw Exception(""),
            content = createdAnnotatedText.content,
            url = createdAnnotatedText.url,
            annotations = listOf()
        )

        val annotatedTextModel = annotatedTextSummaryModelAssembler.toModel(annotatedTextMessage)

        return ResponseEntity
            .created(annotatedTextModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(annotatedTextModel);
    }

    @GetMapping(path = ["/annotatedtexts"])
    fun getAnnotatedTexts(@RequestParam url: String): ResponseEntity<List<EntityModel<AnnotatedTextMessage>>> {
        val decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())

        val annotatedTexts: List<EntityModel<AnnotatedTextMessage>> = annotatedTextRepository
            .findByUrl(decodedUrl)
            .map {
                val id = it.id ?: throw Exception("Need an ID!")
                val annotations = annotationStatsRepository.findByAnnotatedText_Id(id).map {
                        stats -> stats.toMessage()
                }

                AnnotatedTextMessage(
                // TODO: Make ID no longer an optional field
                    id = id,
                    content = it.content,
                    url = it.content,
                    annotations = annotations
                ) }
            .map { annotatedTextSummaryModelAssembler.toModel(it) }

        return ResponseEntity.ok(annotatedTexts)
    }

    @DeleteMapping(path = ["/annotatedtexts/{id}"])
    fun deleteAnnotatedText(@PathVariable id: Int): ResponseEntity<Unit> {
        val annotatedText = annotatedTextRepository.findById(id).orElseThrow { AnnotationNotFoundException(id) }
        val annotations = annotatedText.annotations
        val stats = annotationStatsRepository.findByAnnotatedText_Id(id)

        annotationRepository.deleteAll(annotations)
        annotationStatsRepository.deleteAll(stats)
        annotatedTextRepository.delete(annotatedText)

        return ResponseEntity.noContent().build()
    }
}