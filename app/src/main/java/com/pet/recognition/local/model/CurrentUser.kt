package com.pet.recognition.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CurrentUser(
    val name: String,
    val surname: String,
    val sex: String,
    val email: String,
    val remoteId: String,
    val dateOfBirth: String,
    @PrimaryKey
    val id: Int? = null
)
