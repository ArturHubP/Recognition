package com.pet.recognition.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val sex: String,
    val dateOfBirth: String
)
