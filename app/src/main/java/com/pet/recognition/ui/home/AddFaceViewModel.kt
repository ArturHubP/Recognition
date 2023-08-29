package com.pet.recognition.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.recognition.facerecognizer.AiModel.mobileNet
import com.pet.recognition.facerecognizer.LOG
import com.pet.recognition.facerecognizer.ProcessedImage
import com.pet.recognition.local.dao.Repository
import com.pet.recognition.remote.token.TokenProvider

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddFaceViewModel @Inject constructor(
    private val repo: Repository,
    private val tokenProvider: TokenProvider

) : ViewModel() {
    lateinit var snackbarHost: SnackbarHostState
    val cameraProvider: ProcessCameraProvider by lazy { repo.cameraProviderFuture.get() }
    val showSaveDialog: MutableState<Boolean> = mutableStateOf(false)
    val image = mutableStateOf(ProcessedImage())
    val lensFacing = mutableIntStateOf(CameraSelector.LENS_FACING_BACK)
    val startSearch = mutableStateOf(false)
    val cameraSelector get(): CameraSelector = repo.cameraSelector(lensFacing.value)
    val paint = Paint().apply {
        strokeWidth = 3f
        color = Color.GREEN
    }
    val Context.imageAnalysis
        get() = repo.imageAnalysis(lensFacing.value, paint) { result ->
            runCatching {
                val data = result.getOrNull() ?: return@runCatching
                data.landmarks = data.face?.allLandmarks ?: listOf()
                image.value = data
            }.onFailure { }
        }

    fun onCompose(context: Context, lifecycleOwner: LifecycleOwner, snackbar: SnackbarHostState) = viewModelScope.launch {
        runCatching {
            snackbarHost = snackbar
            if (showSaveDialog.value) return@runCatching
            bindCamera(lifecycleOwner, context.imageAnalysis)
            delay(1000)
            bindCamera(lifecycleOwner, context.imageAnalysis)
            LOG.d("Add Face Screen Composed")
        }.onFailure { LOG.e(it, it.message) }
    }

    fun onDispose() = runCatching {
        cameraProvider.unbindAll()
        LOG.d("Add Face Screen Disposed")
    }.onFailure { LOG.e(it, it.message) }


    fun onFlipCamera() = runCatching {
        lensFacing.value = if (lensFacing.value == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        LOG.d("Camera Flipped lensFacing\t:\t${lensFacing.value}")
    }.onFailure { LOG.e(it, it.message) }

    fun bindCamera(lifecycleOwner: LifecycleOwner, imageAnalysis: ImageAnalysis) = runCatching {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis)
        LOG.d("Camera is bound to lifecycle.")
    }.onFailure { LOG.e(it, it.message) }


    fun showSaveDialog() = runCatching {
        showSaveDialog.value = true
        cameraProvider.unbindAll()
    }.onFailure { LOG.e(it, it.message) }

    fun hideSaveDialog() = runCatching {
        showSaveDialog.value = false
        cameraProvider.unbindAll()
        image.value = ProcessedImage()
    }.onFailure { LOG.e(it, it.message) }

    fun saveFace(){
        startSearch.value = true

    }
    fun saveImage(){
        tokenProvider.setImage(image.value.faceBitmap!!.asImageBitmap())
    }



}
