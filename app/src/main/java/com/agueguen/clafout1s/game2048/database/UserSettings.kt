package com.agueguen.clafout1s.game2048.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity
data class UserSettings(
	@PrimaryKey val id: Int = 0, // There's only one user
	val theme: Int = 0,
	val animations: Boolean = true,
	val music: Boolean = true,
	val soundFX: Boolean = true,
	val optionalFeatures: Boolean = false
)
