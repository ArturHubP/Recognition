package com.pet.recognition.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pet.recognition.local.dao.CurrentUserDao
import com.pet.recognition.local.model.CurrentUser


@Database(
    entities = [CurrentUser::class],
    version = 1,
    exportSchema = false
)
abstract class Database: RoomDatabase() {
    abstract val currentUserDao: CurrentUserDao
}