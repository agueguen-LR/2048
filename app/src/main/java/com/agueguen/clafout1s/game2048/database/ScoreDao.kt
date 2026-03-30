package com.agueguen.clafout1s.game2048.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ScoreDao {
	/**
	 * Save a new Score.
	 */
	@Query("""
	INSERT INTO Score (
		score, highestTile, timeTaken,
		movesTaken, date, boardHeight, boardLength
	)
	VALUES (
		:score, :highestTile, :timeTaken, :movesTaken,
		strftime('%s','now'), :boardHeight, :boardLength
	)
	""")
	fun save(
		score: Long, highestTile: Byte, timeTaken: Long,
		movesTaken: Long, boardHeight: Int, boardLength: Int
	)

	/**
	 * Get all saved scores, ordered by total score descending
	 */
	@Query("SELECT * FROM Score ORDER BY score DESC, timeTaken ASC")
	fun getAll(): List<Score>

	/**
	 * Deletes all saved scores
	 */
	@Query("DELETE FROM Score")
	fun reinitializeAll()

}
