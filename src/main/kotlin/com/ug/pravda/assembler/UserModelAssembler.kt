package com.ug.pravda.assembler

import com.ug.pravda.controller.UserController
import com.ug.pravda.model.User
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Component

@Component
class UserModelAssembler : RepresentationModelAssembler<
        User, EntityModel<User>> {

    override fun toModel(user: User): EntityModel<User> {
        // TODO: Make user ID not an optional field
        val userId = user.id ?: throw Exception("User ID not found")

        return EntityModel.of(user,
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController::class.java)
                    .getUser(userId)
            ).withSelfRel())
    }
}