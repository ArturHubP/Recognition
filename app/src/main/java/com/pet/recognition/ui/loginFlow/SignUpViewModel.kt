package com.pet.recognition.ui.loginFlow

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.pet.recognition.local.dao.CurrentUserRepo
import com.pet.recognition.local.model.CurrentUser
import com.pet.recognition.remote.api.ApiRepository
import com.pet.recognition.remote.api.ApiResponse
import com.pet.recognition.remote.dto.JwtDto
import com.pet.recognition.remote.dto.SignInDto
import com.pet.recognition.remote.dto.UserCreateDto
import com.pet.recognition.remote.dto.UserDto
import com.pet.recognition.remote.token.AuthRepository
import com.pet.recognition.remote.token.TokenProvider
import com.pet.recognition.ui.base.BaseViewModel
import com.pet.recognition.ui.base.CoroutinesErrorHandler
import com.pet.recognition.ui.util.formatDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val apiService: ApiRepository,
    private val tokenProvider: TokenProvider,
    savedStateHandle: SavedStateHandle,
    currentUserRepo: CurrentUserRepo,
): BaseViewModel() {
    var name = MutableStateFlow("")
    var surname = MutableStateFlow("")
    var sex = MutableStateFlow("")
    var email = MutableStateFlow("")
    var password = MutableStateFlow("")
    val dateOfBirth = MutableStateFlow("")
    var passwordConfirm = MutableStateFlow("")


    var loading = MutableStateFlow<ApiResponse<Boolean>>(ApiResponse.Idling)

    var showEmailConfirmDialog = MutableStateFlow(false)

    val usernameExistsResponse = MutableStateFlow<ApiResponse<Boolean>>(ApiResponse.Idling)
    val emailExistsResponse = MutableStateFlow<ApiResponse<Boolean>>(ApiResponse.Idling)


    private val _userCreateResponse = MutableStateFlow<ApiResponse<ResponseBody>>(ApiResponse.Idling)
    val userCreateResponse : StateFlow<ApiResponse<ResponseBody>> get() = _userCreateResponse.asStateFlow()
    private val _loginResponse = MutableStateFlow<ApiResponse<JwtDto>>(ApiResponse.Idling)
    val loginResponse: StateFlow<ApiResponse<JwtDto>> get() = _loginResponse.asStateFlow()

    private val _userResponse = MutableStateFlow<ApiResponse<UserDto>>(ApiResponse.Loading)
    val userResponse = _userResponse.asStateFlow()

    val errorMessage = MutableStateFlow("")

    private val coroutinesErrorHandler = object : CoroutinesErrorHandler {
        override fun onError(message: String) {
            Log.d("error",message)
        }
    }

    init {

        viewModelScope.launch {
            userCreateResponse.collect {
                if (it is ApiResponse.Success){
                    startLogin()
                }
            }
        }
        viewModelScope.launch {
            _loginResponse.collect {
                when (it) {
                    is ApiResponse.Success -> {
                        tokenProvider.saveToken(it.data)
                        getUser()
                    }
                    is ApiResponse.Failure -> loading.emit(
                        ApiResponse.Failure(
                            errorMessage = "Ошибка",
                            code = 400
                        )
                    )
                    else -> {}
                }
            }
        }
        viewModelScope.launch {
            _userResponse.collect { userData ->
                if (userData is ApiResponse.Success) {
                    launch {
                        savedStateHandle["USER_ID"] = userData.data.id
                        currentUserRepo.insertCurrentUser(
                            CurrentUser(
                                name = userData.data.firstName,
                                surname = userData.data.lastName,
                                email = userData.data.email,
                                dateOfBirth = userData.data.dateOfBirth,
                                remoteId = userData.data.id,
                                sex = userData.data.sex,
                                id = 1
                            )
                        )
                        loading.emit(ApiResponse.Success(true))
                    }
                }
            }
        }
    }


    fun signUp() = baseRequest(
        _userCreateResponse,
        coroutinesErrorHandler
    ) {
        authRepository.createUser(
            UserCreateDto(
                firstName = name.value,
                lastName = surname.value,
                sex = sex.value,
                dateOfBirth = formatDate(dateOfBirth.value),
                email = email.value.lowercase(Locale.ROOT),
                password = password.value
            )
        )
    }

    fun startLogin() = baseRequest(
        _loginResponse,
        coroutinesErrorHandler
    ) {
        authRepository.login(
            SignInDto(
                email = email.value,
                password = password.value
            )
        )
    }

    fun checkEmailExist() = baseRequest(
        emailExistsResponse,
        coroutinesErrorHandler
    ){
        apiService.checkEmailExists(email.value)
    }


    fun getUser() = baseRequest(
        _userResponse,
        coroutinesErrorHandler
    ) {
        apiService.getUser()
    }
}