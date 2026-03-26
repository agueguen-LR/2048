package com.agueguen.clafout1s.game2048.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.OnConflictStrategy

@Dao
interface SaveStateDao {
	/**
	 * Create a new SaveState for a game board.
	 * Replaces any previous savestates with the same id
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun create(saveState: SaveState)

	/**
	 * Get the SaveState with the specified id.
	 */
	@Query("SELECT * FROM savestate WHERE id = :id")
	fun get(id: Int): SaveState

	/**
	 * Delete the SaveState
	 */
	@Delete
	fun delete(saveState: SaveState)
}
