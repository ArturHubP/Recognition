package com.pet.recognition.ui.loginFlow


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.pet.recognition.MainViewModel
import com.pet.recognition.remote.api.ApiResponse
import com.pet.recognition.remote.api.LoginErrorCodes
import com.pet.recognition.remote.dto.SignInDto
import com.pet.recognition.ui.base.CoroutinesErrorHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navigateToMainScreen: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    tokenViewModel: TokenViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    var loginState by rememberSaveable { mutableStateOf("") }
    var passwordState by rememberSaveable { mutableStateOf("") }
    val loginResponse by authViewModel.loginResponse.collectAsState()
    val error by authViewModel.error.collectAsState()
    val token by tokenViewModel.token.collectAsState()
    val scrollState  = rememberScrollState()
    val context = LocalContext.current
    if(loginResponse is ApiResponse.Success){
        LaunchedEffect(true){
            tokenViewModel.saveToken((loginResponse as ApiResponse.Success).data)
        }
    }
    if(token?.isNotBlank() == true){
        LaunchedEffect(Unit){
            mainViewModel.getUser()
            mainViewModel.saveUser()
            navigateToMainScreen.invoke()
        }
    }


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        val (c, l, p, b) = createRefs()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
                .constrainAs(c) {
                    top.linkTo(parent.top)
                    bottom.linkTo(b.top)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Вход",
                    style = TextStyle(
                        fontSize = 32.sp,
                        lineHeight = 40.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
                    )
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 28.dp),
                value = loginState,
                onValueChange = { loginState = it },
                label = { Text(text = "Email") }
            )


            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = passwordState,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { passwordState = it },
                label = { Text(text = "Пароль") }
            )

            Row {
                Text(
                    text = when (error) {
                        LoginErrorCodes.USER_PASSWORD_OR_EMAIL_NOT_VALID-> "Неверный логин или пароль"
                        LoginErrorCodes.USER_NOT_ACTIVATED -> "Требуется подтвердить e-mail"
                        LoginErrorCodes.USER_NOT_ENABLED -> ""
                        else -> {
                            ""
                        }
                    },
                    color = Color(0xFFBA1A1A)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .constrainAs(b) {
                    top.linkTo(c.bottom)
                    bottom.linkTo(parent.bottom)
                },
            verticalAlignment = Alignment.Bottom
        ) {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                enabled = loginState.isNotEmpty() && passwordState.isNotEmpty(),
                onClick = {
                    authViewModel.login(
                        SignInDto(loginState, passwordState),
                        object : CoroutinesErrorHandler {
                            override fun onError(message: String) {
                                Toast.makeText(context,"Проверьте подключение к интернету",Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            ) {
                Text(
                    text = "Войти"
                )
            }
        }
    }
}

