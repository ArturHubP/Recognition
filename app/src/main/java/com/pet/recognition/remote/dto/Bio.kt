package com.pet.recognition.remote.dto

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
data class Bio(
    val email: String,
    val firstName: String,
    val lastName: String,
    val sex: String,
    val dateOfBirth: String,
    val id: String,
)
