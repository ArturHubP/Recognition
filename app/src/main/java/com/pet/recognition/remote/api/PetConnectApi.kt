package com.pet.recognition.remote.api

import com.pet.recognition.remote.dto.Amount
import com.pet.recognition.remote.dto.Bio
import com.pet.recognition.remote.dto.EmailConfirmDto
import com.pet.recognition.remote.dto.JwtDto
import com.pet.recognition.remote.dto.RefreshTokenDto
import com.pet.recognition.remote.dto.SignInDto
import com.pet.recognition.remote.dto.Transaction
import com.pet.recognition.remote.dto.UserCreateDto
import com.pet.recognition.remote.dto.UserDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PetConnectApi {

    @POST("/users/sign-up")
    suspend fun createUser(@Body user: UserCreateDto): Response<ResponseBody>

    @GET("/users/me")
    suspend fun getUser(): Response<UserDto>

    @POST("/auth/sign-in")
    suspend fun signIn(@Body signDto: SignInDto): Response<JwtDto>

    @POST("/users/activation")
    suspend fun emailConfirm(@Body emailConfirmDto: EmailConfirmDto) : Response<Boolean>

    @GET("/users/activation/resend")
    suspend fun resendEmailConfirm(@Query("email") email: String) : Response<ResponseBody>

    @POST("/bio")
    suspend fun getReceiver(): Response<Bio>

    @GET("/money/balances")
    suspend fun getBalance(): Response<Amount>

    @POST("/auth/logout")
    suspend fun logout(@Header("deviceId") deviceId: String): Response<ResponseBody>

    @DELETE("/users/")
    suspend fun deleteAccount(): Response<ResponseBody>

    @POST("/auth/refresh-token")
    suspend fun refreshToken(@Body refreshToken: RefreshTokenDto): Response<JwtDto>


    @GET("/users/exist-username")
    suspend fun checkUsernameExists(@Query("username") userName: String): Response<Boolean>

    @GET("/users/exist-email")
    suspend fun checkEmailExists(@Query("email") email: String): Response<Boolean>

    @POST("/money/transactions")
    suspend fun makeTransaction(@Body transaction: Transaction): Response<ResponseBody>
}
