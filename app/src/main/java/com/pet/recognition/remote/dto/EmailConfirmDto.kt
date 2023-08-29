package com.pet.recognition.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmailConfirmDto(
    val email: String,
    val code: String
)
