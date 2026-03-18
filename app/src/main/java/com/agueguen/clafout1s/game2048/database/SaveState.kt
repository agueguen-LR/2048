package com.agueguen.clafout1s.game2048.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SaveState(
	@PrimaryKey val id: Int,
	val board: ByteArray = ByteArray(16),
	val boardHeight: Int = 4,
	val boardLength: Int = 4
)
