package com.pet.recognition.remote.api

import com.pet.recognition.remote.dto.Amount
import com.pet.recognition.remote.dto.Bio
import com.pet.recognition.remote.dto.SignInDto
import com.pet.recognition.remote.dto.Transaction
import com.pet.recognition.remote.dto.UserCreateDto
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody


class ApiRepositoryImpl(
    private val api: PetConnectApi,
    private val apiRequestHelper: ApiRequestHelper
): ApiRepository {
    override fun logout(deviceId: String) = apiRequestHelper.apiRequestFlow {
        api.logout(deviceId)
    }

    override fun signIn(
        deviceId: String,
        token: String,
        signDto: SignInDto
    ) = apiRequestHelper.apiRequestFlow {
        api.signIn(signDto)
    }

    override fun createUser(user: UserCreateDto) = apiRequestHelper.apiRequestFlow {
        api.createUser(user)
    }

    override fun getUser()= apiRequestHelper.apiRequestFlow {
        api.getUser()
    }

    override fun checkUsernameExists(userName: String) = apiRequestHelper.apiRequestFlow {
        api.checkUsernameExists(userName)
    }


    override fun checkEmailExists(email: String) = apiRequestHelper.apiRequestFlow {
        api.checkEmailExists(email)
    }

    override fun getReceiver()= apiRequestHelper.apiRequestFlow {
        api.getReceiver()
    }

    override fun getBalance()= apiRequestHelper.apiRequestFlow {
        api.getBalance()
    }

    override fun maketransaction(transaction: Transaction)= apiRequestHelper.apiRequestFlow {
        api.makeTransaction(transaction)
    }

}