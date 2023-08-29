package com.pet.recognition.ui.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.krishnaZyala.faceRecognition.ui.composable.FaceView
import com.krishnaZyala.faceRecognition.ui.composable.FrameView
import com.pet.recognition.facerecognizer.LOG
import com.pet.recognition.facerecognizer.ProcessedImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFaceScreen(snackbarHostState: SnackbarHostState,vm: AddFaceViewModel = hiltViewModel(),receiverViewModel: ReceiverViewModel = hiltViewModel(),navigateToMainScreen:() -> Unit) {
    val image: ProcessedImage by vm.image
    val lensFacing: Int by vm.lensFacing
    val showSaveDialog: Boolean by vm.showSaveDialog
    val searchStarted by vm.startSearch
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(showSaveDialog, lensFacing) {
        vm.onCompose(context, lifecycleOwner, snackbarHostState)
        onDispose { vm.onDispose() }
    }



    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (!searchStarted) {
                Row(
                    Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    image.frame?.let {
                        FrameView(
                            frame = it,
                            onFlipCamera = vm::onFlipCamera,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    image.face?.let {
                        if (kotlin.math.abs(it.headEulerAngleX) in 0f..30f
                            && kotlin.math.abs(it.headEulerAngleY) in 0f..30f
                            && kotlin.math.abs(it.headEulerAngleZ) in 0f..20f
                            && it.boundingBox.height() >= 150 && it.boundingBox.width() >= 150
                        ) {
                            rememberCoroutineScope().launch {
                                delay(2000)
                                vm.showSaveDialog()
                            }
                        }
                    }
                }
                image.faceBitmap?.let {
                    FaceView(
                        bitmap = it, modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                    vm.saveImage()
                }
            } else {
                vm.hideSaveDialog()
                navigateToMainScreen.invoke()
            }
        }


        if (showSaveDialog) SaveDialog(image, onCancel = vm::hideSaveDialog, onSave = vm::saveFace)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveDialog(
    value: ProcessedImage,
    modifier: Modifier = Modifier,
    positiveBtnText: String = "Искать",
    negativeBtnText: String = "Отмена",
    properties: DialogProperties = DialogProperties(
        decorFitsSystemWindows = false,
        usePlatformDefaultWidth = false
    ),
    content: (@Composable () -> Unit)? = null,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    val newContent = content ?: {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface))
        Card(modifier = modifier) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                value.faceBitmap?.asImageBitmap()?.let {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            it,
                            contentDescription = null,
                            alignment = Alignment.Center,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(250.dp)
                                .padding(40.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp), Arrangement.Center, Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 20.dp),
                    onClick = onSave
                ){
                    Text(text = positiveBtnText.uppercase())
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 20.dp),
                    onClick = onCancel
                ){
                    Text(text = negativeBtnText.uppercase())
                }
            }
        }
    }
    Dialog(onCancel, properties, newContent)
}

