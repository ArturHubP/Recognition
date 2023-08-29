package com.pet.recognition

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.pet.recognition.facerecognizer.ProcessedImage
import com.pet.recognition.local.dao.CurrentUserRepo
import com.pet.recognition.local.model.CurrentUser
import com.pet.recognition.remote.api.ApiRepository
import com.pet.recognition.remote.api.ApiResponse
import com.pet.recognition.remote.dto.Amount
import com.pet.recognition.remote.dto.Bio
import com.pet.recognition.remote.dto.Transaction
import com.pet.recognition.remote.dto.UserDto
import com.pet.recognition.remote.token.TokenProvider
import com.pet.recognition.ui.base.BaseViewModel
import com.pet.recognition.ui.base.CoroutinesErrorHandler
import com.pet.recognition.ui.logout.UserProfileViewModel
import com.pet.recognition.ui.util.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiRepository,
    private val currentUserRepo: CurrentUserRepo,
    private val savedStateHandle: SavedStateHandle,
    val tokenProvider: TokenProvider
): BaseViewModel()  {



    private val coroutinesErrorHandler = object : CoroutinesErrorHandler {
        override fun onError(message: String) {
            Log.d("error",message)
        }
    }
    private val _userResponse = MutableStateFlow<ApiResponse<UserDto>>(ApiResponse.Loading)
    val image = tokenProvider.getImage()
    val balanceResponse = MutableStateFlow<ApiResponse<Amount>>(ApiResponse.Loading)
    val _bioResponse = MutableStateFlow<ApiResponse<Bio>>(ApiResponse.Loading)
    val transactionResponse = MutableStateFlow<ApiResponse<ResponseBody>>(ApiResponse.Loading)
    val balanceAmount = MutableStateFlow(0)
    val processedImage = MutableStateFlow<ImageBitmap?>(null)
    val currentUser = MutableStateFlow<CurrentUser?>(null)
    val receiverUser = MutableStateFlow(Bio("","","","","",""))
    val navToWelcomeScreen = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            image.collect{
                processedImage.value = it
            }
        }
        viewModelScope.launch {
            balanceResponse.collect{
                if(it is ApiResponse.Success){
                    balanceAmount.value = it.data.amount
                }
            }
        }
        viewModelScope.launch {
            tokenProvider.needRelogin().collect {
                navToWelcomeScreen.value = it

            }
        }
        viewModelScope.launch {
            tokenProvider.getRefreshToken().collect{
                if(it.isNullOrBlank()){
                    navToWelcomeScreen.value = true
                }
            }
        }
        getUser()
        getUserFromCache()
        getReceiver()
    }


    fun getUser() = baseRequest(
        _userResponse,
        coroutinesErrorHandler
    ) {
        apiService.getUser()
    }

    private fun getUserFromCache(){
        viewModelScope.launch{
            currentUserRepo.getCurrentUser().collect{
                currentUser.value = it
            }
        }
    }

    fun getReceiverUser()= baseRequest(
        _bioResponse,
        coroutinesErrorHandler
    ){
        apiService.getReceiver()
    }

    fun saveUser() {
        viewModelScope.launch {
            _userResponse.collect{ userData ->
                if(userData is ApiResponse.Success){
                    launch {
                        savedStateHandle.set("USER_ID",userData.data.id)
                        currentUserRepo.insertCurrentUser(
                            CurrentUser(
                                name = userData.data.firstName,
                                surname = userData.data.lastName,
                                sex = userData.data.sex,
                                dateOfBirth = userData.data.dateOfBirth,
                                email = userData.data.email,
                                remoteId = userData.data.id,
                                id = 1
                            )
                        )
                    }
                }

            }
        }
    }

    fun dontNeedRelogin(){
        tokenProvider.dontNeedRelogin()
    }

    fun getBalance()= baseRequest(
        balanceResponse,
        coroutinesErrorHandler
    ){
        apiService.getBalance()
    }

    fun makeTransaction(transaction: Transaction) = baseRequest(
        transactionResponse,
        coroutinesErrorHandler
    ){
        apiService.maketransaction(transaction)
    }

    fun cleanImage(){
        tokenProvider.cleanImage()
        processedImage.value = null
    }

    fun getReceiver(){
        viewModelScope.launch {
            _bioResponse.collect{
                if(it is ApiResponse.Success){
                    receiverUser.value = it.data
                }
            }
        }
    }
}