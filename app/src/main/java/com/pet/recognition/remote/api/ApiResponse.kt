package com.pet.recognition.remote.api

import android.content.Context
import android.util.Log
import com.pet.recognition.remote.dto.ErrorResponse
import com.pet.recognition.remote.token.TokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import retrofit2.Response
import javax.inject.Inject

sealed class ApiResponse<out T> {
    object Loading: ApiResponse<Nothing>()
    object Idling: ApiResponse<Nothing>()

    data class Success<out T>(
        val data: T
    ): ApiResponse<T>()

    data class Failure(
        val timestamp: String? = null,
        val status: String? = null,
        val errorMessage: String,
        val code: Int,
        val details: String? = null
    ): ApiResponse<Nothing>()
}
@OptIn(ExperimentalSerializationApi::class)
class ApiRequestHelper @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val context: Context
) {

    fun <T> apiRequestFlow(call: suspend () -> Response<T>): Flow<ApiResponse<T>> = flow {
        emit(ApiResponse.Loading)

        withTimeoutOrNull(20000L) {
            val response = call()
            try {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        kotlinx.coroutines.delay(60)
                        emit(ApiResponse.Success(data))
                    }
                } else {
                    response.errorBody()?.let { error ->
                        kotlinx.coroutines.delay(60)
                        error.close()
                        val parsedError: ErrorResponse = Json.decodeFromStream(error.byteStream())
                        if (parsedError.code == 401 ) {
                            val refresh = tokenProvider.getNewToken()
                            if (refresh.isSuccessful) {
                                tokenProvider.saveToken(refresh.body()!!)
                                apiRequestFlow(call)
                            }else{
                                tokenProvider.deleteToken()
                                tokenProvider.deleteRefreshTokenToken(relogin = true)
                            }
                        }
                        else if(parsedError.code == 429){
                            apiRequestFlow(call)

                        }else if(parsedError.status == "INTERNAL_SERVER_ERROR"|| parsedError.status == "BAD_REQUEST"){
                                tokenProvider.deleteToken()
                                tokenProvider.deleteRefreshTokenToken(relogin = true)
                        }else{
                            emit(
                                ApiResponse.Failure(
                                    errorMessage = parsedError.message,
                                    code = parsedError.code,
                                    status = parsedError.status,
                                    timestamp = parsedError.timestamp,
                                    details = parsedError.details
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                emit(ApiResponse.Failure(errorMessage = e.message ?: e.toString(), code = 400))
            }
        } ?: emit(ApiResponse.Failure(errorMessage = "Timeout! Please try again.", code = 408))
    }.flowOn(Dispatchers.IO)
}