package com.pet.recognition.ui.loginFlow

import androidx.lifecycle.viewModelScope
import com.pet.recognition.remote.api.ApiResponse
import com.pet.recognition.remote.api.LoginErrorCodes
import com.pet.recognition.remote.dto.JwtDto
import com.pet.recognition.remote.dto.SignInDto
import com.pet.recognition.remote.token.AuthRepository
import com.pet.recognition.ui.base.BaseViewModel
import com.pet.recognition.ui.base.CoroutinesErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
): BaseViewModel() {

    private val _loginResponse = MutableStateFlow<ApiResponse<JwtDto>>(ApiResponse.Loading)
    val loginResponse: StateFlow<ApiResponse<JwtDto>> get() = _loginResponse.asStateFlow()

    val error = MutableStateFlow<LoginErrorCodes?>(null)
    val showEmailPasswordNotValid = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            _loginResponse.collect {response ->
                if(response is ApiResponse.Failure){
                    error.value = LoginErrorCodes.find(response.code)
                    }
                }
            }
        }

    open fun login(auth: SignInDto, coroutinesErrorHandler: CoroutinesErrorHandler) = baseRequest(
        _loginResponse,
        coroutinesErrorHandler
    ) {
        authRepository.login(auth)
    }


}