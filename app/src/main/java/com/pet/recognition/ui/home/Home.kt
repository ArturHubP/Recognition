package com.pet.recognition.ui.home

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.pet.recognition.R
import com.pet.recognition.facerecognizer.ProcessedImage


@Composable
fun Scanner(
    navigateToScanner: () -> Unit,
){
    BoxWithConstraints(modifier = Modifier
        .shadow(elevation = 20.dp, spotColor = Color(0x26474D5E), ambientColor = Color(0x26474D5E))
        .height(166.dp)
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .clip(RoundedCornerShape(20.dp))
        .clickable {
            navigateToScanner.invoke()
        },
        contentAlignment = Alignment.Center
    ){
        Image(imageVector = ImageVector.vectorResource(R.drawable.beard), contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFFFCE76), shape = RoundedCornerShape(size = 20.dp)),
            contentScale = ContentScale.FillHeight)
        Row(
            modifier = Modifier.background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 20.dp))
        ){
            Text(
                text = "Отсканировать лицо",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),
                )
            )
        }
    }

}

@ExperimentalPermissionsApi
@Composable
fun FaceRecognitionScreen(
    navigateToMainScreen:() -> Unit,
    receiverViewModel: ReceiverViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )
    val snackbarHostState = remember { SnackbarHostState() }
    if(cameraPermissionState.status.shouldShowRationale) {
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    if(!cameraPermissionState.status.isGranted){
        CameraPermissionSnackbar {
            val settingsIntent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            context.startActivity(settingsIntent)
        }
    }
    AddFaceScreen(snackbarHostState, navigateToMainScreen = navigateToMainScreen)

}

@Composable
fun CameraPermissionSnackbar(
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Display the snackbar with action
    SnackbarHost(
        modifier = Modifier.fillMaxSize(),
        hostState = snackbarHostState
    ) {
        Snackbar(
            action = {
                TextButton(onClick = onSettingsClick) {
                    Text(text = "Настройки")
                }
            }
        ) {
            Text(text = "Разрешите доступ к камере")
        }
    }
    LaunchedEffect(snackbarHostState) {
        snackbarHostState.showSnackbar("Разрешите доступ к камере")
    }
}