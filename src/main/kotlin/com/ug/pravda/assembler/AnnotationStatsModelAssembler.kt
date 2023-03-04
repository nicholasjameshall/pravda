package com.ug.pravda.assembler

import com.ug.pravda.controller.AnnotationController
import com.ug.pravda.model.AnnotationStatsMessage
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*
import org.springframework.stereotype.Component

@Component
class AnnotationStatsModelAssembler : RepresentationModelAssembler<
        Map<String, List<AnnotationStatsMessage>>, EntityModel<Map<String, List<AnnotationStatsMessage>>>> {

    override fun toModel(annotationStats: Map<String, List<AnnotationStatsMessage>>):
            EntityModel<Map<String, List<AnnotationStatsMessage>>> {
        // TODO: Make annotated text ID not an optional field
        val annotatedTextIds: Set<String> = annotationStats.keys

        return EntityModel.of(annotationStats,
            linkTo(methodOn(AnnotationController::class.java)
                .getAnnotations(annotatedTextIds.toList())).withSelfRel())
    }
}