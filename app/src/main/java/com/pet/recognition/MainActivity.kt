package com.pet.recognition

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.pet.recognition.ui.home.FaceRecognitionScreen
import com.pet.recognition.ui.loginFlow.SignInScreen
import com.pet.recognition.ui.loginFlow.SignUpScreen
import com.pet.recognition.ui.loginFlow.SplashScreen
import com.pet.recognition.ui.loginFlow.TokenViewModel
import com.pet.recognition.ui.loginFlow.WelcomeScreen
import com.pet.recognition.ui.theme.RecognitionTheme
import com.pet.recognition.ui.util.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()
    val tokenViewModel: TokenViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecognitionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val refreshToken by tokenViewModel.refreshToken.collectAsState()
                    val isNavToWelcome by viewModel.navToWelcomeScreen.collectAsState()
                    var startDist by remember{ mutableStateOf(Screen.SplashScreen.route) }

                    if(refreshToken.isNullOrBlank()){
                        LaunchedEffect(Unit) {
                            navController.navigate(Screen.SplashScreen.route){
                                popUpTo(Screen.WelcomeScreen.route) { inclusive = true }
                            }
                            startDist = Screen.SplashScreen.route
                            viewModel.dontNeedRelogin()
                        }
                    }
                    NavHost(
                        navController = navController,
                        startDestination = startDist
                    ) {
                        composable(route = Screen.SplashScreen.route) {
                            SplashScreen(
                                onNavigateWelcomeScreen = { navController.navigate(Screen.WelcomeScreen.route)
                                    startDist = Screen.WelcomeScreen.route
                                },
                                onNavigateMainScreen = { navController.navigate(Screen.MainScreen.route)
                                {
                                    popUpTo(Screen.MainScreen.route) { inclusive = true }
                                }
                                    startDist= Screen.MainScreen.route
                                }
                            )
                        }
                        composable(route = Screen.WelcomeScreen.route) {
                            WelcomeScreen(
                                navigateToSignIn = { navController.navigate(Screen.SignInScreen.route) },
                                navigateToSignUp = { navController.navigate(Screen.SignUpScreen.route) }
                            )
                        }
                        composable(route = Screen.SignUpScreen.route) {
                            SignUpScreen(
                                navigateToMainScreen = {navController.navigate(Screen.MainScreen.route){
                                    popUpTo(Screen.MainScreen.route) { inclusive = true }
                                } }
                            )
                        }
                        composable(route = Screen.SignInScreen.route) {
                            SignInScreen(
                                navigateToMainScreen = {navController.navigate(Screen.MainScreen.route){
                                    popUpTo(Screen.MainScreen.route) { inclusive = true }
                                } }
                            )
                        }
                        composable(route = Screen.MainScreen.route,exitTransition = { slideOutVertically(animationSpec = tween(200), targetOffsetY = {it}) }){
                            BackHandler(true) {}
                            MainScreen(
                                navigateToScanner = {navController.navigate(Screen.Scanner.route)},

                            )
                        }
                        composable(route = Screen.Scanner.route){
                            FaceRecognitionScreen(
                                navigateToMainScreen = {navController.navigate(Screen.MainScreen.route){
                                    popUpTo(Screen.MainScreen.route) { inclusive = true }
                                } }
                            )
                        }
                    }
                }
            }
        }
    }
}

/*
@ExperimentalPermissionsApi
@Composable
fun FaceRecognitionScreen() {
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    when (cameraPermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {

        }
        is PermissionStatus.Denied -> {
            Column {
                val textToShow = if ((cameraPermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                    // If the user has denied the permission but the rationale can be shown,
                    // then gently explain why the app requires this permission
                    "The camera is important for this app. Please grant the permission."
                } else {
                    // If it's the first time the user lands on this feature, or the user
                    // doesn't want to be asked again for this permission, explain that the
                    // permission is required
                    "Camera permission required for this feature to be available. " +
                            "Please grant the permission"
                }
                Text(textToShow)
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Request permission")
                }
            }
        }
    }

}*/
