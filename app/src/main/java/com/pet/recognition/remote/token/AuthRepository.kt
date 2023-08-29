package com.pet.recognition.remote.token


import com.pet.recognition.remote.api.ApiRequestHelper
import com.pet.recognition.remote.api.PetConnectApi
import com.pet.recognition.remote.dto.EmailConfirmDto
import com.pet.recognition.remote.dto.SignInDto
import com.pet.recognition.remote.dto.UserCreateDto
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepository @Inject constructor(
    private val apiService: PetConnectApi,
    private val apiRequestHelper: ApiRequestHelper
){


    fun login(auth: SignInDto) = apiRequestHelper.apiRequestFlow {
        apiService.signIn(auth)
    }

    fun logout() = apiRequestHelper.apiRequestFlow {
        apiService.logout()
    }

    fun deleteAccount() = apiRequestHelper.apiRequestFlow {
        apiService.deleteAccount()
    }

    fun createUser(userCreateDto: UserCreateDto) = apiRequestHelper.apiRequestFlow {
        apiService.createUser(userCreateDto)
    }

    fun emailConfirm(emailConfirmDto: EmailConfirmDto) = apiRequestHelper.apiRequestFlow {
        apiService.emailConfirm(emailConfirmDto)
    }

    fun resendEmailConfirm(email: String) = apiRequestHelper.apiRequestFlow {
        apiService.resendEmailConfirm(email)
    }

}