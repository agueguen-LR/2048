package com.agueguen.clafout1s.game2048

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.agueguen.clafout1s.game2048.database.*

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Log.i("GAMEBOARD", "Starting")
      val gameBoard = GameBoard()
			for (i in (0..3)) {
				Log.i("GAMEBOARD", gameBoard.toString()+"\n")
				gameBoard.swipeLeft()
				Log.i("GAMEBOARD", gameBoard.toString()+"\n")
				gameBoard.swipeRight()
				Log.i("GAMEBOARD", gameBoard.toString()+"\n")
				gameBoard.swipeUp()
				Log.i("GAMEBOARD", gameBoard.toString()+"\n")
				gameBoard.swipeDown()
			}
      // val db = AppDatabase.getDatabase(this)
      //
      // val saveStateDao = db.saveStateDao()
      // val saveState = SaveState(1, ByteArray(16), 4, 4)
      // saveStateDao.create(saveState)
      // Log.i("MainActivity", saveStateDao.get(1).toString())
      // saveStateDao.delete(saveState)
      //
      // val scoreDao = db.scoreDao()
      // scoreDao.save(4, 4, 12, 1, 4, 4)
      // scoreDao.save(8, 8, 15, 2, 4, 4)
      // scoreDao.save(6, 8, 18, 3, 4, 4)
      // Log.i("MainActivity", scoreDao.getAll().toString())
      //
      // scoreDao.reinitializeAll()
      // Log.i("MainActivity", scoreDao.getAll().toString())
    }
  }
}
