package com.agueguen.clafout1s.game2048.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity
data class Score(
	@PrimaryKey(autoGenerate = true) val id: Int,
	val score: Long,
	val highestTile: Long,
	val timeTaken: Long,
	val movesTaken: Long,
	val date: Long,
	val boardHeight: Int,
	val boardLength: Int,
)
