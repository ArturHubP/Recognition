package com.pet.recognition.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val timestamp: String? = null,
    val status: String? = null,
    val code: Int,
    val details: String? = null,
    val message: String
)
