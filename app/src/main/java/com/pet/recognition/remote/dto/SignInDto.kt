package com.pet.recognition.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignInDto(
    val email: String,
    val password: String
)
