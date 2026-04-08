package com.agueguen.clafout1s.game2048.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SaveState(
	@PrimaryKey val id: Int,
	val board: ByteArray,
	val boardHeight: Int,
	val boardLength: Int,
	val movesTaken: Long,
	val timeTaken: Long
)
