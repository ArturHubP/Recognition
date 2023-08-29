package com.example.petconnect.ui.mainSceen.components.logout

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.recognition.remote.token.TokenProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val tokenProvider: TokenProvider
) : ViewModel(){
    companion object{
        val ACCESS = booleanPreferencesKey("ACCESS_NOTIFICATION")
    }

    var permissionState = mutableStateOf<Boolean?>(null)
        private set

    fun savePermission(context: Context, isEnabled: Boolean){
        viewModelScope.launch {
            tokenProvider.savePermission(isEnabled)
        }
    }

    fun getPermission(context: Context){
        viewModelScope.launch {
            tokenProvider.getPermission().collect{
                permissionState.value = it
            }
        }
    }

}