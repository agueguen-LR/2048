package com.agueguen.clafout1s.game2048

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
    val boardState: ByteArray = board.data
    val emptyTiles = ArrayList<Int>()
		for (i in 0..<boardState.size) {
			if (boardState[i] == 0.toByte()){
				emptyTiles.add(i)
			}
		}
    if (emptyTiles.isEmpty()) return false // Game over
    boardState[emptyTiles.random()] = listOf(1, 1, 1, 2).random().toByte()
    return true
  }

	private fun swipe(views: Sequence<ByteView>, viewLength: Int, reversed: Boolean = false): Boolean {
		var store = ArrayDeque<Byte>()
		var alreadyMergedFlag: Boolean
		val scanForwardsRange = if (reversed) (viewLength - 1 downTo 0) else (0 until viewLength)
		val scanBackwardsRange = if (reversed) (0 until viewLength) else (viewLength - 1 downTo 0)

		for (view in views) {
			alreadyMergedFlag = false


			for (i in scanForwardsRange){
				var currentByte = view[i]
				if (currentByte == 0.toByte()) {
					continue
				} 

				// merge consecutive tiles if previous tile wasn't already a merge
				if (store.lastOrNull() == currentByte && !alreadyMergedFlag) {
					store[store.lastIndex] = store[store.lastIndex].inc()
					alreadyMergedFlag = true
				} else {
					store.addLast(currentByte)
					alreadyMergedFlag = false
				}
			}

			// build new row state
			for (i in scanBackwardsRange) {
				view[i] = store.removeLastOrNull() ?: 0
			}
		}
		return createNewTile()
  }

	fun swipeLeft(): Boolean {
		return swipe(board.rows(), board.width, false)
  }

	fun swipeRight(): Boolean {
		return swipe(board.rows(), board.width, true)
  }

	fun swipeUp(): Boolean {
		return swipe(board.columns(), board.height, true)
	}

	fun swipeDown(): Boolean {
		return swipe(board.columns(), board.height, false)
	}

  fun getGameState(): ByteArray {
    return board.data
  }

  override fun toString(): String {
    return board.toString()
  }
}
