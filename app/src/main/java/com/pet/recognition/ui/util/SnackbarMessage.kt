package com.pet.recognition.ui.util

data class SnackbarMessage(
    val message: String,
    val actionButtonText: String,
    val action: (() -> Unit?)? = null
)
