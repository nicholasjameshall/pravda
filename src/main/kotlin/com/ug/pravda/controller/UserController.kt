package com.ug.pravda.controller

import com.ug.pravda.assembler.UserModelAssembler
import com.ug.pravda.dao.UserRepository
import com.ug.pravda.exception.UserNotFoundException
import com.ug.pravda.model.CreateUserRequest
import com.ug.pravda.model.User
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userModelAssembler: UserModelAssembler,
    private val userRepository: UserRepository
) {
    @PostMapping(path = ["/users"])
    fun createUser(@RequestBody request: CreateUserRequest) : ResponseEntity<EntityModel<User>> {
        val newUser = User(
            name = request.name
        )

        userRepository.save(newUser)
        val userModel = userModelAssembler.toModel(newUser)
        return ResponseEntity
            .created(userModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(userModel);
    }

    @GetMapping(path = ["/users/{id}"])
    fun getUser(@RequestParam id: Int): ResponseEntity<EntityModel<User>> {
        val user = userRepository.findById(id).orElseThrow { UserNotFoundException(id) }

        return ResponseEntity.ok(
            userModelAssembler.toModel(user))
    }
}