package com.ug.pravda.assembler

import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*
import org.springframework.stereotype.Component
import com.ug.pravda.model.AnnotationMessage

@Component
class AnnotationModelAssembler : RepresentationModelAssembler<AnnotationMessage, EntityModel<AnnotationMessage>> {
    override fun toModel(annotation: AnnotationMessage): EntityModel<AnnotationMessage> {
        return EntityModel.of(annotation)
    }
}