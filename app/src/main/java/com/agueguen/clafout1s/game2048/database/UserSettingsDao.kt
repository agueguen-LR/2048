package com.agueguen.clafout1s.game2048.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM UserSettings WHERE id = 0")
    fun getUserSettings(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(settings: UserSettings)
}
