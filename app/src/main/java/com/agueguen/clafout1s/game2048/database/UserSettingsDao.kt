package com.agueguen.clafout1s.game2048.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM UserSettings LIMIT 1")
    fun getUserSettings(): UserSettings?

    @Query("SELECT * FROM UserSettings LIMIT 1")
    fun getUserSettingsFlow(): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(settings: UserSettings)
}
