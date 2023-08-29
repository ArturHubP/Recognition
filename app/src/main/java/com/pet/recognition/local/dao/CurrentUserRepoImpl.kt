package com.pet.recognition .local.dao

import com.pet.recognition.local.dao.CurrentUserDao
import com.pet.recognition.local.dao.CurrentUserRepo
import com.pet.recognition.local.model.CurrentUser
import kotlinx.coroutines.flow.Flow

class CurrentUserRepoImpl(
    private val dao: CurrentUserDao
): CurrentUserRepo {


    override suspend fun insertCurrentUser (user: CurrentUser) {
        return dao.insertCurrentUser(user)
    }

    override suspend fun getCurrentUser(): Flow<CurrentUser> {
        return dao.getCurrentUser()
    }

}