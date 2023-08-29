package com.pet.recognition.ui.mainSceen.components.logout

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petconnect.ui.mainSceen.components.logout.PermissionViewModel


@Composable
fun PermissionDialog(
    context: Context,
    permissionViewModel: PermissionViewModel = hiltViewModel()
) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = true
            ),
            title = { Text("Уведомления") },
            text = {
                Text("Чтобы наше приложение могло предоставить вам наилучший опыт, мы хотели бы попросить ваше разрешение на отправку уведомлений.")
            },
            onDismissRequest = {
                openDialog.value = true

           },

            dismissButton = {
                FilledTonalButton(
                    onClick = {
                        openDialog.value = false
                        permissionViewModel.savePermission(context, isEnabled = false)
                              },
                ) {
                    Text(text = "Нет")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        permissionViewModel.savePermission(context, isEnabled = true)
                        openDialog.value = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        startActivity(context, intent, null)

                    },
                ) {
                    Text(text = "Да",color = Color.White)

                }
            }
        )
    }
}