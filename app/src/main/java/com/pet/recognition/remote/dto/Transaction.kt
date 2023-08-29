package com.pet.recognition.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val amount: Int,
    val userToId: String
)