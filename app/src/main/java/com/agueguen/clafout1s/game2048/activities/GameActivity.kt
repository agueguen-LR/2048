package com.agueguen.clafout1s.game2048.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Lifecycle

import com.agueguen.clafout1s.game2048.GameInterface
import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.database.SaveState
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont
import com.agueguen.clafout1s.game2048.utilities.stateListToByteArray
import com.agueguen.clafout1s.game2048.AudioManager
import com.agueguen.clafout1s.game2048.R

class GameActivity : AbstractActivity2048(
	modifier = Modifier.fillMaxSize().focusable().padding(top = 50.dp, bottom = 20.dp)
) {
	private lateinit var gameInterface: GameInterface
	private lateinit var showEndDialog: MutableState<Boolean>
	private lateinit var showResetDialog: MutableState<Boolean>
	private lateinit var showSaveScoreDialog: MutableState<Boolean>
	private var winCondition: Byte = 11 // 2048
    private lateinit var buttonColors: ButtonColors

	@Composable
	override fun ScreenContent(){
		val saveStateDao = AppDatabase.getDatabase(this).saveStateDao()
		val context = LocalContext.current
		if (intent.getBooleanExtra("continue", false)) {
			gameInterface = remember { GameInterface(saveStateDao.get(0)!!) }
		} else {
			gameInterface = remember { GameInterface(
				intent.getIntExtra("width", 4),
				intent.getIntExtra("height", 4)
			)}
		}
		val boardHeight = gameInterface.boardHeight
		val boardWidth = gameInterface.boardWidth

		buttonColors = ButtonColors(
			MaterialTheme.colorScheme.primaryContainer,
			MaterialTheme.colorScheme.primary,
			MaterialTheme.colorScheme.errorContainer,
			MaterialTheme.colorScheme.error
		)

		winCondition = when (boardWidth * boardHeight) {
			9 -> 6 // 3*3 goal is 64
			16 -> 11 // 4*4 goal is 2048
			25 -> 14 // 5*5 goal is 16384
			36 -> 17 // 6*6 goal is 131072
			else -> 11 // just default to 2048, it's the name of the game after all
		}
		showResetDialog = remember { mutableStateOf(false) }
		showSaveScoreDialog = remember { mutableStateOf(false) }
		showEndDialog = remember { mutableStateOf(false) }

		Column(
			modifier = Modifier.fillMaxHeight(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {

			Button(
				modifier = Modifier.padding(20.dp),
				colors = buttonColors,
				onClick = {
					AudioManager.playSound(context, R.raw.click)
					showResetDialog.value = true
				}
			){
				Text(
					"Reset",
					fontFamily = blockyFont,
					fontSize = 30.sp
				)
			}

			Text(
				"Score: ${gameInterface.score.value}",
				fontSize = 40.sp,
				color = MaterialTheme.colorScheme.onPrimary,
				modifier = Modifier.padding(20.dp)
			)

			Row(
				modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
				horizontalArrangement = Arrangement.Center,
			){
				val tileSize = calculateTileSize(boardWidth, boardHeight, 10.dp)
				gameInterface.GameInterfaceComposable(tileSize, 10.dp)
			}
		}

		val hasLost = gameInterface.playerHasLost.value
		val highestTile = gameInterface.highestTile.value
		LaunchedEffect(hasLost, highestTile) {
			if (hasLost || highestTile >= winCondition) {
				showEndDialog.value = true
			}
		}

		if (showResetDialog.value) ResetGridDialog()
		if (showSaveScoreDialog.value) SaveScoreDialog()
		if (showEndDialog.value) EndGameDialog()

		val lifecycleOwner = LocalLifecycleOwner.current
		DisposableEffect(lifecycleOwner) {
			val observer = LifecycleEventObserver { _, event -> 
				when (event) {
					Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
						saveStateDao.create(SaveState(
							0, // this activity uses saveState slot 0
							stateListToByteArray(gameInterface.gameBoard.getGameGrid().data),
							boardHeight,
							boardWidth,
							gameInterface.movesTaken.value,
							gameInterface.timer.value
						))
					}
					else -> {}
				}
			}

			lifecycleOwner.lifecycle.addObserver(observer)

			onDispose {
				lifecycleOwner.lifecycle.removeObserver(observer)
			}
		}
	}

	@Composable
	fun calculateTileSize(
		boardWidth: Int,
		boardHeight: Int,
		marginSize: Dp
	): Dp {
		val screenHeight = LocalWindowInfo.current.containerSize.height
		val screenWidth = LocalWindowInfo.current.containerSize.width

		val marginNbX = boardWidth + 3
		val marginNbY = boardHeight + 3

		var tileSize: Dp = 80.dp

		val density = LocalDensity.current

		val totalWidthPx = with(density) {
			boardWidth * tileSize.toPx() + marginNbX * marginSize.toPx()
		}

		val totalHeightPx = with(density) {
			boardHeight * tileSize.toPx() + marginNbY * marginSize.toPx()
		}

		tileSize = when {
			totalWidthPx > screenWidth && totalHeightPx > screenHeight -> {
				if (totalWidthPx >= totalHeightPx) {
					with(density) {
						((screenWidth - marginNbX * marginSize.toPx()) / boardWidth).toDp()
					}
				} else {
					with(density) {
						((screenHeight - marginNbY * marginSize.toPx()) / boardHeight).toDp()
					}
				}
			}

			totalWidthPx > screenWidth -> {
				with(density) {
					((screenWidth - marginNbX * marginSize.toPx()) / boardWidth).toDp()
				}
			}

			totalHeightPx > screenHeight -> {
				with(density) {
					((screenHeight - marginNbY * marginSize.toPx()) / boardHeight).toDp()
				}
			}

			else -> tileSize
		}

		return tileSize
	}

	@Composable
	private fun EndGameDialog(){
		val context = LocalContext.current
		val win = gameInterface.highestTile.value >= winCondition
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
						colors = buttonColors,
						onClick = {
							saveCurrentScore()
							context.startActivity(Intent(context, ScoreboardActivity::class.java))
							showEndDialog.value = false
						}
					) {
						Text("To scoreboard",fontFamily = blockyFont, fontWeight = FontWeight.Bold)
					}
				}
				else{
					Button(
						colors = buttonColors,
						onClick = {
							saveCurrentScore()
							context.startActivity(Intent(context, ScoreboardActivity::class.java))
							showEndDialog.value = false
						}
					) {
						Text(
							"Add to scoreboard",
							fontFamily = blockyFont,
							fontWeight = FontWeight.Bold
						)
					}
				}
			},
			dismissButton = {
				Button(
					colors = buttonColors,
					onClick = {
						showEndDialog.value = false
					}
				) {
					Text("No",fontFamily = blockyFont, fontWeight = FontWeight.Bold)
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
					colors = buttonColors,
					onClick = {
						showResetDialog.value = false
						showSaveScoreDialog.value = true
					}
				) { Text("Yes",fontFamily = blockyFont, fontWeight = FontWeight.Bold) }
			},
			dismissButton = {
				Button(
					colors = buttonColors,
					onClick = { showResetDialog.value = false }
				) { Text("No",fontFamily = blockyFont, fontWeight = FontWeight.Bold) }
			}
		)
	}

	/**
	 * Dialog choice to save the score, after pressing the reset button.
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
					colors = buttonColors,
					onClick = {
						saveCurrentScore()
						gameInterface.resetBoard()
						showSaveScoreDialog.value = false
					}
				) { Text("Yes",fontFamily = blockyFont, fontWeight = FontWeight.Bold) }
			},
			dismissButton = {
				Button(
					colors = buttonColors,
					onClick = { 
						gameInterface.resetBoard()
						showSaveScoreDialog.value = false 
					}
				) { Text("No",fontFamily = blockyFont, fontWeight = FontWeight.Bold) }
			}
		)
	}

	private fun saveCurrentScore(){
		val db = AppDatabase.getDatabase(this)
		val scoreDao = db.scoreDao()
		// TODO: Record time in a variable
		scoreDao.save(
			gameInterface.score.value,
			gameInterface.highestTile.value,
			gameInterface.timer.value,
			gameInterface.movesTaken.value,
			gameInterface.boardWidth,
			gameInterface.boardHeight
		)
	}
}

