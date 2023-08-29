package com.pet.recognition.remote.token

import androidx.compose.ui.graphics.ImageBitmap
import com.pet.recognition.remote.dto.JwtDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface TokenProvider {
    fun getToken(): Flow<String>
    fun getImage():Flow<ImageBitmap?>
    fun setImage(imageBitmap: ImageBitmap)
    fun cleanImage()
    fun getRefreshToken(): Flow<String>
    suspend fun saveToken(token: JwtDto)

    fun needRelogin(): Flow<Boolean>
    fun dontNeedRelogin()
    suspend fun deleteToken()
    suspend fun deleteRefreshTokenToken(relogin: Boolean? = null)

    suspend fun savePermission(isEnabled: Boolean)

    fun getPermission(): Flow<Boolean?>

    suspend fun getNewToken(): Response<JwtDto>
}