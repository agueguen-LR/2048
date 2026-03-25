import com.agueguen.clafout1s.game2048.database.SaveState

class GameBoard {
  var boardState: ByteArray
  var boardLength: Int
  var boardHeight: Int

  init {
    boardLength = 4
    boardHeight = 4
    boardState = ByteArray(16)
    assert(createNewTile())
  }

  constructor(boardLength: Int = 4, boardHeight: Int = 4) {
    assert(boardLength > 1 && boardHeight > 1)
    this.boardLength = boardLength
    this.boardHeight = boardHeight
    boardState = ByteArray(boardLength * boardHeight)
    assert(createNewTile())
  }

  constructor(saveState: SaveState) {
    this.boardLength = saveState.boardLength
    this.boardHeight = saveState.boardHeight
    this.boardState = saveState.board
  }

  private fun createNewTile(): Boolean {
    val emptyTiles =
            boardState.mapIndexed { index, value -> if (value == 0.toByte()) index else null }
    if (emptyTiles.isEmpty()) return false
    boardState[(0 until emptyTiles.size).random()] = listOf(1, 1, 1, 2).random().toByte()
    return true
  }

  fun getGameState(): ByteArray {
    return boardState
  }

  override fun toString(): String {
    return boardState.joinToString(prefix = "[", postfix = "]")
  }
}
