package com.pet.recognition.remote.dto

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
data class UserDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val sex: String,
    val dateOfBirth: String,
    val id: String,
    val createdAt: String? = null,
    val updateAt: String? = null
)
