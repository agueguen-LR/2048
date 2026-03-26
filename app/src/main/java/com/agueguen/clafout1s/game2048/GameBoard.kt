package com.agueguen.clafout1s.game2048

import com.agueguen.clafout1s.game2048.database.SaveState
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

	fun swipeLeft(): Boolean {
		var store = ArrayDeque<Byte>()
		var alreadyMergedFlag: Boolean
		for (row in board.rows()) {
			alreadyMergedFlag = false

			// scan row and compute end state
			for (i in 0..<row.length){
				var currentByte = row[i]
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
			for (i in row.length-1 downTo 0) {
				row[i] = store.removeLastOrNull() ?: 0
			}
		}
		return createNewTile()
  }

  fun getGameState(): ByteArray {
    return board.data
  }

  override fun toString(): String {
    return board.toString()
  }
}
