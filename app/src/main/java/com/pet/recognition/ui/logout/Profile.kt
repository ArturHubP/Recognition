package com.pet.recognition.ui.logout

import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.pet.recognition.R
import com.pet.recognition.remote.api.ApiResponse
import com.pet.recognition.ui.loginFlow.TokenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    paddingValues: PaddingValues,
    navigateToMainScreen: () -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel(),
    tokenViewModel: TokenViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser
    val context = LocalContext.current
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    var requestPermission by remember { mutableStateOf(false) }
    val logoutResponse by viewModel.logoutResponse.collectAsState()
    val scrollState = rememberScrollState()
    var showDeleteAlert by remember { mutableStateOf(false) }
    LaunchedEffect(true){
        requestPermission = notificationManager.areNotificationsEnabled()
    }

    LaunchedEffect(logoutResponse){
        if(logoutResponse is ApiResponse.Success){
            navigateToMainScreen.invoke()
        }
    }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (pic, i,) = createRefs()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .constrainAs(i) {
                        top.linkTo(pic.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column() {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                                text = currentUser?.name ?: "",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight(400),
                                    letterSpacing = 0.5.sp,
                                )
                            )
                        }
                        Divider(modifier = Modifier.fillMaxWidth())
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MailOutline,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                                text = currentUser?.email ?: "",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight(400),
                                    letterSpacing = 0.5.sp,
                                )
                            )
                        }
                        Divider(modifier = Modifier.fillMaxWidth())
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            TextButton(onClick = {
                                showDeleteAlert = true

                            }) {
                                Text(
                                    modifier = Modifier.padding(start = 4.dp),
                                    text = "Удалить аккаунт",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight(500),
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 0.1.sp,
                                    )
                                )
                            }
                        }
                        Divider(modifier = Modifier.fillMaxWidth())
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {

                            TextButton(onClick = {
                                viewModel.startLogout()

                            }) {
                                Text(
                                    modifier = Modifier.padding(start = 4.dp),
                                    text = "Выход",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight(500),
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 0.1.sp,
                                    )
                                )
                            }
                        }
                    }
                }
            }

                    Image(
                        modifier = Modifier
                            .sizeIn(30.dp, 148.dp)
                            .constrainAs(pic) {
                                top.linkTo(parent.top, 1.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(i.top, 1.dp)
                            },
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight
                    )


        }

    if(showDeleteAlert) {
        AlertDialog(
            title = {Text(text = "Подтвердите действие")},
            text = { Text(text = "Вы уверены, что действительно хотите удалить Аккаунт?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.startLogout()
                }) {
                    Text(text = "Удалить", color = MaterialTheme.colorScheme.error)
                }},
            dismissButton = {
                TextButton(onClick = { showDeleteAlert = false }) {
                    Text(text = "Отмена")
                }},
            onDismissRequest = { showDeleteAlert = false})
    }
}



