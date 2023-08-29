package com.pet.recognition.ui.loginFlow

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel

import com.pet.recognition.ui.util.ValidationService.validateEmail
import com.pet.recognition.ui.util.ValidationService.validateLogin
import com.pet.recognition.ui.util.ValidationService.validatePassword
import com.pet.recognition.remote.api.ApiResponse
import com.pet.recognition.ui.util.DatePickerComponent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navigateToMainScreen: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel(),
) {

    val name by viewModel.name.collectAsState()
    val surname by viewModel.surname.collectAsState()
    val sex by viewModel.sex.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val dateOfBirth by viewModel.dateOfBirth.collectAsState()
    val passwordConfirm by viewModel.passwordConfirm.collectAsState()
    val emailCheckResponse by viewModel.emailExistsResponse.collectAsState()
    val loginResponse by viewModel.loginResponse.collectAsState()
    val userCreateResponse by viewModel.userCreateResponse.collectAsState()
    val isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var showDatePicker by remember{mutableStateOf(false)}

    val context = LocalContext.current



    LaunchedEffect(userCreateResponse) {
        when (userCreateResponse) {
            is ApiResponse.Success -> {
                navigateToMainScreen.invoke()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    context,
                    "Упс, что-то пошло не так. Проверьте подключение к интернету и повторите попытку",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    if (emailCheckResponse is ApiResponse.Success) {
        LaunchedEffect(true) {
            if ((emailCheckResponse as ApiResponse.Success).data) {
                Toast.makeText(context, "Данный email уже существует", Toast.LENGTH_SHORT).show()
            }
        }
    }
    if(showDatePicker){
        DatePickerComponent(
            startDate = LocalDate.now(),
            onSnap = {
                viewModel.dateOfBirth.value = it.format(DateTimeFormatter.ofPattern("dd.MM.yyy"))
            }, onDismiss = {
                showDatePicker = false
            })
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        val (cr, b) = createRefs()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .constrainAs(cr) {
                    top.linkTo(parent.top)
                    bottom.linkTo(b.top)
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
            ) {
                Text(
                    text = "Регистрация",
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
                    .padding(bottom = 8.dp),
                value = name,
                singleLine = true,
                onValueChange = { viewModel.name.value = it },
                label = { Text(text = "Имя") }
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = surname,
                singleLine = true,
                onValueChange = { viewModel.surname.value = it },
                label = { Text(text = "Фамилия") }
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = sex,
                singleLine = true,
                onValueChange = { viewModel.sex.value = it },
                label = { Text(text = "Пол") }
            )
            Row(
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .clickable {
                        showDatePicker = true
                    },
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    value = dateOfBirth,
                    readOnly = true,
                    enabled = false,
                    label = { Text(text = "Дата рождения") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledBorderColor = Color.Black
                    ),
                    onValueChange = {},

                    )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(bottom = 8.dp),
                value = email,
                singleLine = true,
                onValueChange = { viewModel.email.value = it },
                label = { Text(text = "Email") }
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                value = password,
                singleLine = true,
                onValueChange = { viewModel.password.value = it },
                visualTransformation = PasswordVisualTransformation(),
                isError = if (password.isNotBlank()) !validatePassword(password) else false,
                label = { Text(text = "Пароль") }
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                text = "Пароль должен содержать не менее 8-ми символов, в том числе цифры, знаки, прописные и строчные буквы латинского алфавита",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF3F4945),
                )
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(bottom = 8.dp),
                value = passwordConfirm,
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                onValueChange = { viewModel.passwordConfirm.value = it },
                label = { Text(text = "Подтвердите пароль") },
                isError = if (passwordConfirm.isNotBlank()) password != passwordConfirm else false
            )
            Row() {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = when (loginResponse) {
                        is ApiResponse.Success -> "Good"
                        is ApiResponse.Idling -> "idling"
                        is ApiResponse.Failure -> "Ошибка, проверьте подключения к интернету."
                        is ApiResponse.Loading -> "loading"
                    },
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 20.dp)
                .constrainAs(b) {
                    top.linkTo(cr.bottom)
                    bottom.linkTo(parent.bottom)
                },
            onClick = {
                viewModel.signUp()
            },
            enabled = validateLogin(name) && validateEmail(email) && validatePassword(
                password
            ) && password == passwordConfirm && name.length >= 3
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Зарегистрироваться",
                color = Color.White,
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.1.sp,
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}






