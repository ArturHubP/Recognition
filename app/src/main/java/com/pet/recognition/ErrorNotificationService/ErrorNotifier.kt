package com.pet.recognition.ErrorNotificationService

import android.content.Context
import android.widget.Toast
import com.pet.recognition.ErrorNotificationService.ErrorNotifierInterface

class ErrorNotifier(private val context: Context): ErrorNotifierInterface {

    override fun showNoInternetNotification() {
        Toast.makeText(context,"Проверьте подключение к интернету",Toast.LENGTH_SHORT).show()
    }

    override fun showError() {
        Toast.makeText(context,"Произошла непредвиденная ошибка попробуйте позже",Toast.LENGTH_SHORT).show()
    }

    // Rest of the code...
}