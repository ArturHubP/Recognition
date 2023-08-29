package com.pet.recognition.ui.logout

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.pet.recognition.local.dao.CurrentUserRepo
import com.pet.recognition.local.model.CurrentUser
import com.pet.recognition.remote.api.ApiResponse
import com.pet.recognition.remote.token.AuthRepository
import com.pet.recognition.remote.token.TokenProvider
import com.pet.recognition.ui.base.BaseViewModel
import com.pet.recognition.ui.base.CoroutinesErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val currentUserRepo: CurrentUserRepo,
    private val authRepository: AuthRepository,
    private val tokenProvider: TokenProvider,
): BaseViewModel() {
    var currentUser = mutableStateOf<CurrentUser?>(null)
        private set
    var deviceId = mutableStateOf("")
    private val _logoutResponse = MutableStateFlow<ApiResponse<ResponseBody>>(ApiResponse.Idling)
    val logoutResponse: StateFlow<ApiResponse<ResponseBody>> get() = _logoutResponse.asStateFlow()
    private val coroutinesErrorHandler = object : CoroutinesErrorHandler {
        override fun onError(message: String) {
            Log.d("error",message)
        }
    }

    init {
        getCurrentUser()
        deleteUser()
    }
    private fun getCurrentUser () {
        viewModelScope.launch {
            currentUserRepo.getCurrentUser().collect{
                currentUser.value = it
            }
        }
    }

    private fun deleteUser(){
        viewModelScope.launch{
            _logoutResponse.collect{
                if(it is ApiResponse.Success){
                    tokenProvider.deleteToken()
                    tokenProvider.deleteRefreshTokenToken(relogin = false)
                }
            }
        }
    }

    fun startLogout() = baseRequest(
        _logoutResponse,
        coroutinesErrorHandler
    ) {
        authRepository.logout()
    }
}