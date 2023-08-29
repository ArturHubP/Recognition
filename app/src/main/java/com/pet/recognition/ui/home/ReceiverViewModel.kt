package com.pet.recognition.ui.home

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ReceiverViewModel @Inject constructor() : ViewModel() {
    val imageData = MutableStateFlow(ImageBitmap(50,50))

    fun setImage(image: ImageBitmap){
        imageData.value = image
    }
}