package com.pet.recognition.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pet.recognition.local.model.CurrentUser
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentUser(user: CurrentUser)

    @Query("SELECT * FROM currentuser WHERE id = 1")
    fun getCurrentUser(): Flow<CurrentUser>
}