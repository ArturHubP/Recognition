package com.pet.recognition.local.dao

import com.pet.recognition.local.model.CurrentUser
import kotlinx.coroutines.flow.Flow

interface CurrentUserRepo {

    suspend fun insertCurrentUser(user: CurrentUser)
    suspend fun getCurrentUser(): Flow<CurrentUser>
}