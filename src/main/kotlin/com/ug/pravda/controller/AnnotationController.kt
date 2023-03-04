package com.ug.pravda.controller

import com.ug.pravda.assembler.AnnotationModelAssembler
import com.ug.pravda.assembler.AnnotationStatsModelAssembler
import com.ug.pravda.dao.AnnotatedTextRepository
import com.ug.pravda.dao.AnnotationRepository
import com.ug.pravda.dao.AnnotationStatsRepository
import com.ug.pravda.dao.UserRepository
import com.ug.pravda.exception.AnnotatedTextNotFoundException
import com.ug.pravda.exception.AnnotationNotFoundException
import com.ug.pravda.exception.UserNotFoundException
import com.ug.pravda.model.*
import com.ug.pravda.model.Annotation
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AnnotationController(
    private val annotatedTextRepository: AnnotatedTextRepository,
    private val userRepository: UserRepository,
    private val annotationRepository: AnnotationRepository,
    private val annotationStatsRepository: AnnotationStatsRepository,
    private val annotationModelAssembler: AnnotationModelAssembler,
    private val annotationStatsModelAssembler: AnnotationStatsModelAssembler
) {
    @PatchMapping(path = ["/annotations"])
    fun updateAnnotation(@RequestBody annotation: AnnotationMessage):
            EntityModel<AnnotationMessage> {

        val loadedAnnotation = annotationRepository
            .findById(annotation.id)
            .orElseThrow { AnnotationNotFoundException(annotation.id) }

        if (!isValidUpdate(annotation, loadedAnnotation)) {
            throw Exception("Can only update annotation sentiment") // TODO: Add custom exception
        }

        if (annotation.sentiment == loadedAnnotation.sentiment) {
            throw Exception("Sentiment has not changed") // TODO: Add custom exception
        }

        val annotatedText = annotatedTextRepository.findById(annotation.annotatedTextId).orElseThrow {
            AnnotatedTextNotFoundException(annotation.annotatedTextId)
        }

        val user = userRepository.findById(annotation.submitterId).orElseThrow {
            UserNotFoundException(annotation.submitterId)
        }

        val updatedAnnotation = Annotation(
            id = annotation.id,
            annotatedText = annotatedText,
            type = annotation.type,
            sentiment = annotation.sentiment,
            submitter = user
        )

        updateAnnotationStats(updatedAnnotation)
        annotationRepository.save(updatedAnnotation)
        return annotationModelAssembler.toModel(annotation)
    }

    private fun updateAnnotationStats(annotation: Annotation) {
        val loadedAnnotationStats = annotationStatsRepository
            .findByAnnotatedTextAndType(
                annotation.annotatedText, annotation.type)

        if (loadedAnnotationStats.isNotEmpty()) {
            val stats = loadedAnnotationStats.first()
            val newVoteCount: Int = when (annotation.sentiment) {
                Sentiment.NEGATIVE -> stats.votes - 1
                Sentiment.POSITIVE -> stats.votes + 1
            }

            if (newVoteCount == 0) {
                annotationStatsRepository.delete(stats)
            } else {
                stats.votes = newVoteCount
                annotationStatsRepository.save(stats)
            }
        } else {
            val newAnnotationStats = AnnotationStats(
                annotatedText = annotation.annotatedText,
                type = annotation.type,
                votes = if (annotation.sentiment == Sentiment.POSITIVE) 1 else -1
            )
            annotationStatsRepository.save(newAnnotationStats)
        }
    }

    private fun isValidUpdate(new: AnnotationMessage, loaded: Annotation): Boolean {
        return new.annotatedTextId == loaded.annotatedText.id &&
                new.type == loaded.type &&
                new.submitterId == loaded.submitter.id
    }

    @DeleteMapping(path = ["/annotations/{id}"])
    fun deleteAnnotation(@PathVariable id: Int): ResponseEntity<Unit> {
        val annotation = annotationRepository.findById(id).orElseThrow { AnnotationNotFoundException(id) }
        val annotatedText = annotation.annotatedText
        annotatedText.annotations.remove(annotation)
        annotationRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping(path = ["/annotations"])
    fun createAnnotation(@RequestBody request: CreateAnnotationRequest):
            EntityModel<AnnotationMessage> {

        val annotatedText: AnnotatedText = annotatedTextRepository
            .findById(request.textId)
            .orElseThrow { AnnotatedTextNotFoundException(request.textId) }

        // TODO: check user ID is correct with user credentials
        val submitter: User = userRepository
            .findById(request.userId)
            .orElseThrow { UserNotFoundException(request.userId) }

        val newAnnotation = Annotation(
            type = request.type,
            annotatedText = annotatedText,
            submitter = submitter,
            sentiment = request.sentiment
        )

        if (annotationExists(newAnnotation)) {
            throw Exception("Annotation already exists") }

        val createdAnnotation = annotationRepository.save(newAnnotation)
        updateAnnotationStats(createdAnnotation)
        annotatedText.annotations.add(createdAnnotation)
        annotatedTextRepository.save(annotatedText)

        return annotationModelAssembler.toModel(createdAnnotation.toMessage())
    }

    private fun annotationExists(annotation: Annotation): Boolean {
        val annotationMatcher: ExampleMatcher = ExampleMatcher.matching()
            .withIgnorePaths("id")
            .withIgnorePaths("sentiment")
            .withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact())
            .withMatcher("annotatedText", ExampleMatcher.GenericPropertyMatchers.exact())
            .withMatcher("submitter", ExampleMatcher.GenericPropertyMatchers.exact())
        val example: Example<Annotation> = Example.of(annotation, annotationMatcher)
        return annotationRepository.exists(example)
    }

    @GetMapping(path = ["/annotations"])
    fun getAnnotations(@RequestParam textIds: List<String>):
            EntityModel<Map<String, List<AnnotationStatsMessage>>> {

        val annotationStatsToTextId: Map<String, List<AnnotationStatsMessage>> = textIds
            .associateBy({ it }, {
                annotationStatsRepository.findByAnnotatedText_Id(1).map {
                    it.toMessage()
                }
            })

        return annotationStatsModelAssembler.toModel(annotationStatsToTextId)
    }
}