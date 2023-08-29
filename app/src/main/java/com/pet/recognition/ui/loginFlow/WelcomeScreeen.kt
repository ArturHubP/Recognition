package com.pet.recognition.ui.loginFlow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pet.recognition.R


@Composable
fun WelcomeScreen(
    navigateToSignIn: () -> Unit,
    navigateToSignUp: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier.weight(1f)
        ){
            Box(modifier = Modifier.fillMaxSize()){

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Хакатон",
                        style = TextStyle(
                            fontSize = 32.sp,
                            lineHeight = 40.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF191C1B),
                            textAlign = TextAlign.Center,
                        )
                    )
                    Text(
                        text = "Привет!",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFF191C1B),
                            textAlign = TextAlign.Center,
                        )
                    )
                }


            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                modifier = Modifier.height(44.dp),
                onClick = navigateToSignIn
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Войти",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(500),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.1.sp,
                    ),
                    textAlign = TextAlign.Center,
                )

            }
            Button(
                modifier = Modifier.height(44.dp),
                onClick = navigateToSignUp
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

}