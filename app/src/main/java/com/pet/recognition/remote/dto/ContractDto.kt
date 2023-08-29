package com.pet.recognition.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContractDto(
    val fullName: String,
    val contract: String
)
