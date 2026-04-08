package com.agueguen.clafout1s.game2048.activities

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.agueguen.clafout1s.game2048.GameInterface
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont

class VersusActivity : AbstractActivity2048(
	modifier = Modifier.fillMaxSize().focusable().padding(top = 40.dp, bottom = 50.dp)
) {
	private lateinit var player1: GameInterface
	private lateinit var player2: GameInterface
	private lateinit var buttonColors: ButtonColors
	private lateinit var showLoseDialog: MutableState<Boolean>
	private lateinit var showTimeUpDialog: MutableState<Boolean>
	private lateinit var show2048Dialog: MutableState<Boolean>

	@Composable
	override fun ScreenContent(){
		val tileSize = calculateTileSize(4, 4, 10.dp)
		player1 = remember { GameInterface(4, 4) }
		player2 = remember { GameInterface(4, 4) }
		showLoseDialog = remember { mutableStateOf(false) }
		showTimeUpDialog = remember { mutableStateOf(false) }
		show2048Dialog = remember { mutableStateOf(false) }

		buttonColors = ButtonColors(
			MaterialTheme.colorScheme.primaryContainer,
			MaterialTheme.colorScheme.primary,
			MaterialTheme.colorScheme.errorContainer,
			MaterialTheme.colorScheme.error
		)

		Column(
			modifier = Modifier.fillMaxHeight(),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {

			Row(
				modifier = Modifier.fillMaxWidth().weight(1f).graphicsLayer { scaleX = -1f; scaleY = -1f },
				horizontalArrangement = Arrangement.Center,
			){
				player2.GameInterfaceComposable(tileSize, 5.dp)
			}

			if (intent.getBooleanExtra("timer", false)) {
				Text(
					"P1↓: ${player1.score.value} | ${180 - player1.timer.value} | P2↑: ${player2.score.value}",
					fontSize = 20.sp,
					color = MaterialTheme.colorScheme.onPrimary,
				)
			} else {
				Text(
					"P1↓: ${player1.score.value} | P2↑: ${player2.score.value}",
					fontSize = 20.sp,
					color = MaterialTheme.colorScheme.onPrimary,
				)
			}


			Row(
				modifier = Modifier.fillMaxWidth().weight(1f),
				horizontalArrangement = Arrangement.Center,
			){
				player1.GameInterfaceComposable(tileSize, 5.dp)
			}
		}

		if (showLoseDialog.value) {
			LoseDialog()
		}
		val hasLost1 = player1.playerHasLost.value
		val hasLost2 = player2.playerHasLost.value
		LaunchedEffect(hasLost1, hasLost2) {
			if (hasLost1 || hasLost2) {
				showLoseDialog.value = true
			}
		}

		if (showTimeUpDialog.value) {
			TimeUpDialog()
		} else {
			_2048Dialog()
		}
		if (intent.getBooleanExtra("timer", false)) {
			LaunchedEffect(player1.timer.value) {
				if (180 - player1.timer.value <= 0) {
					showTimeUpDialog.value = true
				}
			}
		} else {
			LaunchedEffect(player1.score.value, player2.score.value) {
				if (player1.highestTile.value >= 11 || player2.highestTile.value >= 11) {
					show2048Dialog.value = true
				}
			}
		}

	}

	@Composable
	fun calculateTileSize(
		boardWidth: Int,
		boardHeight: Int,
		marginSize: Dp
	): Dp {
		var screenHeight = LocalWindowInfo.current.containerSize.height
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
	private fun LoseDialog(){
		val context = LocalContext.current
		AlertDialog(
			onDismissRequest = { showLoseDialog.value = false },
			title = {
				if(player1.playerHasLost.value) Text("Player 1 has lost, Player 2 wins !")
				else Text("Player 2 has lost, Player 1 wins !")
			},
			text = {
				Text("New Game ?")
			},
			confirmButton = {
				Button(
					colors = buttonColors,
					onClick = {
						player1.resetBoard()
						player2.resetBoard()
						showLoseDialog.value = false
					}
				) {
					Text(
						"New Game",
						fontFamily = blockyFont,
						fontWeight = FontWeight.Bold
					)
				}
			},
			dismissButton = {
				Button(
					colors = buttonColors,
					onClick = {
						showLoseDialog.value = false
						context.startActivity(Intent(context, MainMenuActivity::class.java))
					}
				) {
					Text(
						"Main Menu",
						fontFamily = blockyFont,
						fontWeight = FontWeight.Bold
					)
				}
			}
		)
	}

	@Composable
	private fun TimeUpDialog(){
		val context = LocalContext.current
		AlertDialog(
			onDismissRequest = { showTimeUpDialog.value = false },
			title = {
				Text("Time's up !")
			},
			text = {
				if (player1.score.value > player2.score.value) {
					Text("Player 1 wins with ${player1.score.value} points !")
				} else if (player2.score.value > player1.score.value) {
					Text("Player 2 wins with ${player2.score.value} points !")
				} else {
					Text("It's a tie with ${player1.score.value} points each !")
				}
			},
			confirmButton = {
				Button(
					colors = buttonColors,
					onClick = {
						player1.resetBoard()
						player2.resetBoard()
						showTimeUpDialog.value = false
					}
				) {
					Text(
						"New Game",
						fontFamily = blockyFont,
						fontWeight = FontWeight.Bold
					)
				}
			},
			dismissButton = {
				Button(
					colors = buttonColors,
					onClick = {
						showTimeUpDialog.value = false
						context.startActivity(Intent(context, MainMenuActivity::class.java))
					}
				) {
					Text(
						"Main Menu",
						fontFamily = blockyFont,
						fontWeight = FontWeight.Bold
					)
				}
			}
		)
	}

	@Composable
	private fun _2048Dialog(){
		val context = LocalContext.current
		AlertDialog(
			onDismissRequest = { show2048Dialog.value = false },
			title = {
				Text("2048 reached !")
			},
			text = {
				if (player1.highestTile.value >= 11) {
					Text("Player 1 wins by reaching 2048 with ${player1.score.value} points !")
				}else {
					Text("Player 2 wins by reaching 2048 with ${player2.score.value} points !")
				}
			},
			confirmButton = {
				Button(
					colors = buttonColors,
					onClick = {
						player1.resetBoard()
						player2.resetBoard()
						show2048Dialog.value = false
					}
				) {
					Text(
						"New Game",
						fontFamily = blockyFont,
						fontWeight = FontWeight.Bold
					)
				}
			},
			dismissButton = {
				Button(
					colors = buttonColors,
					onClick = {
						show2048Dialog.value = false
						context.startActivity(Intent(context, MainMenuActivity::class.java))
					}
				) {
					Text(
						"Main Menu",
						fontFamily = blockyFont,
						fontWeight = FontWeight.Bold
					)
				}
			}
		)
	}

}

