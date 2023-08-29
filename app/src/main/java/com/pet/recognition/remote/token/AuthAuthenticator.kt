package com.pet.recognition.remote.token

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: TokenProvider,
): Authenticator {


        override fun authenticate(route: Route?, response: Response): Request? {
            val token = runBlocking {
                tokenManager.getToken().first()
            }
            return runBlocking {
                val newToken = tokenManager.getNewToken()

                if (!newToken.isSuccessful || newToken.body() == null) {
                    tokenManager.deleteToken()
                }

                newToken.body()?.let { jwtDto ->
                    tokenManager.saveToken(jwtDto)
                    val originalRequest = response.request()
                    val url = originalRequest.url()
                    val method = originalRequest.method()
                    val body = originalRequest.body()

                    val authorizedRequest = Request.Builder()
                        .url(url)
                        .method(method, body)
                        .header("Authorization", "Bearer ${jwtDto.accessToken}")
                        .build()

                    authorizedRequest
                }
            }
        }
}