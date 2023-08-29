package com.pet.recognition.ui.util

sealed class Screen(val route: String) {
    object SignInScreen: Screen("auth")
    object MainScreen: Screen("dashboard")
    object SplashScreen: Screen("splashscreen")
    object Scanner: Screen("scanner")

    object WelcomeScreen: Screen("welcome_screen")

    object MapScreen: Screen("map_screen")

    object SignUpScreen: Screen("signup")
}