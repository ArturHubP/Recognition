package com.pet.recognition.remote.api

import com.pet.recognition.remote.dto.Amount
import com.pet.recognition.remote.dto.Bio
import com.pet.recognition.remote.dto.JwtDto
import com.pet.recognition.remote.dto.SignInDto
import com.pet.recognition.remote.dto.Transaction
import com.pet.recognition.remote.dto.UserCreateDto
import com.pet.recognition.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody


interface ApiRepository {
    fun logout(deviceId: String): Flow<ApiResponse<ResponseBody>>


    fun signIn(deviceId: String,token: String ,signDto: SignInDto): Flow<ApiResponse<JwtDto>>

    fun createUser(user: UserCreateDto): Flow<ApiResponse<ResponseBody>>


    fun getUser(): Flow<ApiResponse<UserDto>>

    fun checkUsernameExists(userName: String): Flow<ApiResponse<Boolean>>

    fun checkEmailExists(email: String): Flow<ApiResponse<Boolean>>

    fun getReceiver(): Flow<ApiResponse<Bio>>
    fun getBalance(): Flow<ApiResponse<Amount>>

    fun maketransaction(transaction: Transaction): Flow<ApiResponse<ResponseBody>>
}