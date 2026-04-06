package com.agueguen.clafout1s.game2048

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme

class GameActivity : Activity2048(
	modifier = Modifier.fillMaxSize().focusable().padding(top = 50.dp, bottom = 20.dp)
) {
	private val gameInterface: GameInterface = GameInterface()
	private lateinit var showEndDialog: MutableState<Boolean>
	private lateinit var showResetDialog: MutableState<Boolean>
	private lateinit var showSaveScoreDialog: MutableState<Boolean>
	private val winCondition: Byte = 11 //TODO Make this adapted to grid size

	@Composable
	override fun ScreenContent(){
		showResetDialog = remember { mutableStateOf(false) }
		showSaveScoreDialog = remember { mutableStateOf(false) }
		showEndDialog = remember { mutableStateOf(false) }

		Column(
			modifier = Modifier.fillMaxHeight(),
			verticalArrangement = Arrangement.Center
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Center,
			){
				gameInterface.GameInterfaceComposable()
			}

			Button(
				onClick = {
					showResetDialog.value = true
				}
			){
				Text("Reset the game")
			}

			Text("Score: ${gameInterface.getScore().value}")
		}

		val hasLost = gameInterface.hasPlayerLost().value
		val highestTile = gameInterface.getHighestTile().value
		LaunchedEffect(hasLost, highestTile) {
			if (hasLost || highestTile >= winCondition) {
				showEndDialog.value = true
			}
		}

		if (showResetDialog.value) ResetGridDialog()
		if (showSaveScoreDialog.value) SaveScoreDialog()
		if (showEndDialog.value) EndGameDialog()
	}

	@Composable
	private fun EndGameDialog(){
		val context = LocalContext.current
		val win = gameInterface.getHighestTile().value >= winCondition
		AlertDialog(
			onDismissRequest = { showEndDialog.value = false },
			title = {
				if(win) Text("You won !")
				else Text("You lost...")
			},
			text = {
				if(win) Text("You can check your score in the scoreboard.")
				else Text("Do you want to add your score to the scoreboard ?")
			},
			confirmButton = {
				if(win) {
					Button(
						onClick = {
							saveCurrentScore()
							context.startActivity(Intent(context, ScoreboardActivity::class.java))
							showEndDialog.value = false
						}
					) {
						Text("To scoreboard")
					}
				}
				else{
					Button(
						onClick = {
							saveCurrentScore()
							context.startActivity(Intent(context, ScoreboardActivity::class.java))
							showEndDialog.value = false
						}
					) {
						Text("Add to scoreboard")
					}
				}
			},
			dismissButton = {
				Button(
					onClick = {
						showEndDialog.value = false
					}
				) {
					Text("No")
				}
			}
		)
	}

	@Composable
	private fun ResetGridDialog(){
		AlertDialog(
			onDismissRequest = {
				showResetDialog.value = false
			},
			title = { Text("Reseting grid") },
			text = { Text("Do you really want to reset the grid ?") },
			confirmButton = {
				Button(
					onClick = {
						showResetDialog.value = false
						showSaveScoreDialog.value = true
					}
				) { Text("Yes") }
			},
			dismissButton = {
				Button(
					onClick = { showResetDialog.value = false }
				) { Text("No") }
			}
		)
	}

	/**
	 * Dialog choice to save the score, after pressing the reset button.
	 *
	 * @param mutableBoolean: The function must be called on an if statement on the value of mutableBoolean, for the AlertDialog to be able to close.
	 */
	@Composable
	private fun SaveScoreDialog(){
		AlertDialog(
			onDismissRequest = { 
				gameInterface.resetBoard()
				showSaveScoreDialog.value = false 
			},
			title = { Text("Saving score") },
			text = { Text("Do you want to save your current score in the scoreboard ?") },
			confirmButton = {
				Button(
					onClick = {
						saveCurrentScore()
						gameInterface.resetBoard()
						showSaveScoreDialog.value = false
					}
				) { Text("Yes") }
			},
			dismissButton = {
				Button(
					onClick = { 
						gameInterface.resetBoard()
						showSaveScoreDialog.value = false 
					}
				) { Text("No") }
			}
		)
	}

	private fun saveCurrentScore(){
		val db = AppDatabase.getDatabase(this)
		val scoreDao = db.scoreDao()
		// TODO: Record time in a variable
		scoreDao.save(
			gameInterface.getScore().value,
			gameInterface.getHighestTile().value,
			0,
			gameInterface.getMovesTaken().value,
			4,
			4
		)
	}
}

