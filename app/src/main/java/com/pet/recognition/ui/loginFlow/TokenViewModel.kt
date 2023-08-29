package com.pet.recognition.ui.loginFlow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.recognition.remote.dto.JwtDto
import com.pet.recognition.remote.token.TokenProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(
    private val tokenManager: TokenProvider,
): ViewModel() {

    val token = MutableStateFlow<String?>("")
    val refreshToken = MutableStateFlow<String?>("1")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.getToken()
                .collect {
                withContext(Dispatchers.Main) {
                    token.value = it
                }
            }
        }
        viewModelScope.launch {
            tokenManager.getRefreshToken().collect {
                refreshToken.value = it
            }
        }
    }

    fun saveToken(token: JwtDto) {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.saveToken(token)
        }
    }

    fun deleteToken() {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.deleteToken()
        }
    }
    fun deleteRefreshToken() {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.deleteRefreshTokenToken()
        }
    }
}