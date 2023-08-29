package com.pet.recognition.ui.loginFlow

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun SplashScreen(
    onNavigateWelcomeScreen:() -> Unit,
    onNavigateMainScreen: () -> Unit,
    tokenViewModel: TokenViewModel = hiltViewModel()
) {
    val token by tokenViewModel.token.collectAsState()
    LaunchedEffect(token) {
        Log.d("Tag",token.toString())// Get the latest token value
        if (!token.isNullOrBlank()) {
            onNavigateMainScreen.invoke()
        } else {
            onNavigateWelcomeScreen.invoke()
        }
    }
}