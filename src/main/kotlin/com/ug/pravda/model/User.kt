package com.ug.pravda.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class User (
        var name: String,
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Int? = null,
)

data class CreateUserRequest (
        val name: String
)