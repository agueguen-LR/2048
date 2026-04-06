package com.agueguen.clafout1s.game2048.utilities

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.agueguen.clafout1s.game2048.database.SaveState

/**
 * A simple wrapper around a ByteArray to represent a 2D grid of bytes.
 *
 * @param data The underlying data of the grid, stored as a SnapshotStateList<Byte> for Compose state management.
 * @param width The number of columns in the grid.
 * @param height The number of rows in the grid.
 */
data class ByteGrid(val data: SnapshotStateList<Byte>, val width: Int, val height: Int) {
  operator fun get(row: Int, col: Int): Byte {
    return data[row + col * height]
  }

	operator fun get(row: Int): ByteView {
		return ByteView(data, row*width, width)
	}

  constructor(width: Int, height: Int) : this(byteArrayToStateList(ByteArray(width * height)), width, height)

  constructor(saveState: SaveState) : this(byteArrayToStateList(saveState.board), saveState.boardLength, saveState.boardHeight)

  /**
   * Returns a sequence of ByteViews, each representing a row of the grid. Each ByteView provides
   * efficient access to the bytes in that row without copying the underlying data.
   *
   * @return A sequence of ByteViews for each row in the grid.
   */
  fun rows(): Sequence<ByteView> = sequence {
    for (row in 0 until height) {
      yield(ByteView(data, row * width, width))
    }
  }

  /**
   * Returns a sequence of ByteViews, each representing a column of the grid. Each ByteView provides
   * efficient access to the bytes in that column without copying the underlying data.
   *
   * @return A sequence of ByteViews for each column in the grid.
   */
  fun columns(): Sequence<ByteView> = sequence {
    for (col in 0 until width) {
      yield(ByteView(data, col, height, width))
    }
  }

	override fun toString(): String {
		return buildString {
			for (row in 0..<height) {
				for (col in 0..<width) {
					append("%02X ".format(data[row * width + col]))
				}
				appendLine()
			}
		}
	}
}
