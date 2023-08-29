package com.pet.recognition.remote.token

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.petconnect.ui.mainSceen.components.logout.PermissionViewModel
import com.pet.recognition.remote.RemoteConsts
import com.pet.recognition.remote.api.PetConnectApi
import com.pet.recognition.remote.dto.JwtDto
import com.pet.recognition.remote.dto.RefreshTokenDto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


class TokenProviderImpl(private val context: Context): TokenProvider{
    companion object{
        private val TOKEN_KEY = stringPreferencesKey("jwt")
        private val TOKEN_KEY_REFRESH = stringPreferencesKey("refresh")
    }
    private val Context.dataStore by preferencesDataStore("my_data_store_name")
    val needRelogin = MutableStateFlow(false)

    val imageBitmap = MutableStateFlow<ImageBitmap?>(null)


    override fun getToken(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            Log.d("preferences",preferences.toString())
            preferences[TOKEN_KEY]?: ""
        }
    }

    override fun setImage(image: ImageBitmap) {
        imageBitmap.value = image
    }
    override fun getImage(): Flow<ImageBitmap?> {
        return imageBitmap
    }

    override fun cleanImage() {
        imageBitmap.value = null
    }


    override fun getRefreshToken(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            Log.d("preferences",preferences.toString())
            preferences[TOKEN_KEY_REFRESH]?: ""
        }
    }

    override suspend fun savePermission(isEnabled: Boolean){
        context.dataStore.edit { preferences ->

        }

    }

    override fun getPermission(): Flow<Boolean?> {
        return context.dataStore.data.map { preferences ->
            Log.d("preferences",preferences.toString())
            preferences[PermissionViewModel.ACCESS]
        }
    }


    override suspend fun saveToken(token: JwtDto) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token.accessToken
            preferences[TOKEN_KEY_REFRESH] = token.refreshToken
        }
    }

    override fun needRelogin(): Flow<Boolean> = needRelogin
    override fun dontNeedRelogin() {
        needRelogin.value = false
    }

    override suspend fun deleteToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
    override suspend fun deleteRefreshTokenToken(relogin: Boolean?) {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY_REFRESH)
            relogin?: run {
                needRelogin.value = true
            }
        }
    }
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getNewToken(): retrofit2.Response<JwtDto> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(RemoteConsts.BASE_URL)
            .addConverterFactory(Json.asConverterFactory(MediaType.parse("application/json")!!))
            .client(okHttpClient)
            .build()
        val service = retrofit.create(PetConnectApi::class.java)
        val refreshToken = getRefreshToken().first()
        return service.refreshToken(refreshToken = RefreshTokenDto(refreshToken))
    }
}