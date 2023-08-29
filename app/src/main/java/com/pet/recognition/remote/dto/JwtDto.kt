package com.pet.recognition.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class JwtDto(
    val accessToken: String,
    val refreshToken: String
)
