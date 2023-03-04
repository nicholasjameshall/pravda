package com.ug.pravda.assembler

import com.ug.pravda.controller.AnnotatedTextController
import com.ug.pravda.model.AnnotatedTextMessage
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*
import org.springframework.stereotype.Component

@Component
class AnnotatedTextSummaryModelAssembler : RepresentationModelAssembler<
        AnnotatedTextMessage, EntityModel<AnnotatedTextMessage>> {

    override fun toModel(annotatedTextMessage: AnnotatedTextMessage): EntityModel<AnnotatedTextMessage> {
        return EntityModel.of(annotatedTextMessage,
                linkTo(methodOn(AnnotatedTextController::class.java)
                        .getAnnotatedTexts(annotatedTextMessage.url)).withSelfRel())
    }
}