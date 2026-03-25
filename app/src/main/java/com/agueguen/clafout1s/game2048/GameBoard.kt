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
    val emptyTiles =
            boardState.mapIndexed { index, value -> if (value == 0.toByte()) index else null }
    if (emptyTiles.isEmpty()) return false // Game over
    boardState[(0 until emptyTiles.size).random()] = listOf(1, 1, 1, 2).random().toByte()
    return true
  }

  //  fun swipeLeft(): Boolean {
  // }

  fun getGameState(): ByteArray {
    return board.data
  }

  override fun toString(): String {
    return board.toString()
  }
}
