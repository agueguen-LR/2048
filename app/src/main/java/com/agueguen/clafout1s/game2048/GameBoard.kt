package com.agueguen.clafout1s.game2048

import android.util.Log
import com.agueguen.clafout1s.game2048.database.SaveState
import com.agueguen.clafout1s.game2048.utilities.ByteView
import com.agueguen.clafout1s.game2048.utilities.ByteGrid

class GameBoard {
  private var board: ByteGrid

  init {
    board = ByteGrid(4, 4)
    assert(createNewTile())
  }

  constructor(boardLength: Int = 4, boardHeight: Int = 4) {
    assert(boardLength > 1 && boardHeight > 1)
    board = ByteGrid(boardLength, boardHeight)
    assert(createNewTile())
  }

  constructor(saveState: SaveState) {
    board = ByteGrid(saveState)
  }

  private fun createNewTile(): Boolean {
    val emptyTiles = ArrayList<Int>()
		for (i in 0..<board.data.size) {
			if (board.data[i] == 0.toByte()){
				emptyTiles.add(i)
			}
		}
    if (emptyTiles.isEmpty()) return false // Game over
    board.data[emptyTiles.random()] = listOf(1, 1, 1, 2).random().toByte()
    return true
  }

	private fun swipe(views: Sequence<ByteView>, viewLength: Int, reversed: Boolean = false): Boolean {
		var store = ArrayDeque<Byte>()
		var alreadyMergedFlag: Boolean
		val scanRange = if (reversed) (0 until viewLength) else (viewLength - 1 downTo 0)

		for (view in views) {
			alreadyMergedFlag = false

			for (i in scanRange){
				var currentByte = view[i]
				if (currentByte == 0.toByte()) {
					continue
				} 

				// merge consecutive tiles if previous tile wasn't already a merge
				if (store.firstOrNull() == currentByte && !alreadyMergedFlag) {
					store[0] = store[0].inc()
					alreadyMergedFlag = true
				} else {
					store.addFirst(currentByte)
					alreadyMergedFlag = false
				}
			}

			// build new row state
			for (i in scanRange) {
				view[i] = store.removeLastOrNull() ?: 0
			}
		}
		return createNewTile()
  }
	
	/**
	 * Swipe left, merging consecutive tiles of the same value and shifting all tiles in the direction of the swipe.
	 *
	 * @return true if a new tile could be created, false if the board is full and the game is over.
	 */
	fun swipeLeft(): Boolean {
		return swipe(board.rows(), board.width, false)
  }

	/**
	 * Swipe right, merging consecutive tiles of the same value and shifting all tiles in the direction of the swipe.
	 *
	 * @return true if a new tile could be created, false if the board is full and the game is over.
	 */
	fun swipeRight(): Boolean {
		return swipe(board.rows(), board.width, true)
  }

	/**
	 * Swipe up, merging consecutive tiles of the same value and shifting all tiles in the direction of the swipe.
	 *
	 * @return true if a new tile could be created, false if the board is full and the game is over.
	 */
	fun swipeUp(): Boolean {
		return swipe(board.columns(), board.height, true)
	}

	/**
	 * Swipe down, merging consecutive tiles of the same value and shifting all tiles in the direction of the swipe.
	 *
	 * @return true if a new tile could be created, false if the board is full and the game is over.
	 */
	fun swipeDown(): Boolean {
		return swipe(board.columns(), board.height, false)
	}

	/**
	 * Returns the current state of the game board as a ByteGrid.
	 *
	 * @return A ByteGrid representing the current state of the game board, where each byte corresponds to a tile value's power of two (0 for empty).
	 */
	fun getGameGrid(): ByteGrid {
		return board
	}

  override fun toString(): String {
    return board.toString()
  }
}
