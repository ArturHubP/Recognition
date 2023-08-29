package com.pet.recognition.ui.util

object ValidationService {
    fun validateLogin(login: String): Boolean{
        return login.isNotBlank()
    }

    fun validateEmail(email: String): Boolean{
        return email.contains(regex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"))
    }

    fun validatePassword(password: String): Boolean{
        return password.contains(Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#&()–[{}]:;',?/*~\$^+=<>.\\\"%_|`]).{8,20}\$"))
    }
}