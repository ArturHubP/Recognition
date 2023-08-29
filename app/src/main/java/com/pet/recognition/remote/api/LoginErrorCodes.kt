package com.pet.recognition.remote.api

enum class LoginErrorCodes (val code:Int) {
    USER_NOT_ACTIVATED (1003),
    USER_NOT_ENABLED (1002),
    USER_PASSWORD_OR_EMAIL_NOT_VALID (1001);

    companion object {
        fun find(value: Int): LoginErrorCodes? = LoginErrorCodes.values().find { it.code == value }
    }
}