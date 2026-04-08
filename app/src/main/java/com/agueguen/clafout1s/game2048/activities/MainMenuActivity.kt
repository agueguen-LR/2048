package com.agueguen.clafout1s.game2048.activities

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

import com.agueguen.clafout1s.game2048.AudioManager
import com.agueguen.clafout1s.game2048.R
import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont

class MainMenuActivity : AbstractActivity2048() {
	private lateinit var showGridSizeDialog: MutableState<Boolean>
	private lateinit var showVersusDialog: MutableState<Boolean>
	private lateinit var buttonColors: ButtonColors

	@Composable
	fun InitColors(){
		buttonColors = ButtonColors(
			MaterialTheme.colorScheme.primaryContainer,
			MaterialTheme.colorScheme.primary,
			MaterialTheme.colorScheme.errorContainer,
			MaterialTheme.colorScheme.error
		)
	}

	@Composable
	override fun ScreenContent(){
		InitColors()
		showGridSizeDialog = remember { mutableStateOf(false) }
		showVersusDialog = remember { mutableStateOf(false) }
		val context = LocalContext.current
		val gameActivityIntent = Intent(context, GameActivity::class.java)
		val database = AppDatabase.getDatabase(context)
		val userSettings = database.userSettingsDao().getUserSettings()
		val continueSaveState = database.saveStateDao().getAsFlow(0).collectAsState(initial = null)

		Column (
			modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize(),
			verticalArrangement = Arrangement.SpaceEvenly
		) {
			Text(
				text = "2048",
				fontSize = 70.sp,
				fontFamily = blockyFont,
				fontWeight = FontWeight.Bold,
				style = TextStyle(
					brush = Brush.linearGradient(
						listOf(MaterialTheme.colorScheme.onTertiary ,MaterialTheme.colorScheme.tertiary)
					)
				),
				modifier = Modifier.padding(vertical=30.dp).fillMaxWidth(),
				textAlign = TextAlign.Center,
			)

			Column(
				verticalArrangement = Arrangement.SpaceEvenly,
				modifier = Modifier.weight(2F)
			) {
				if (continueSaveState.value != null) {
					menuButton("CONTINUE", onClick = { 
						gameActivityIntent.putExtra("continue", true)
						AudioManager.playSound(context, R.raw.click)
						context.startActivity(gameActivityIntent)
					})
				}
				menuButton("START", onClick = { 
					AudioManager.playSound(context, R.raw.click)
					if (userSettings!!.optionalFeatures) {
						showGridSizeDialog.value = true
					} else {
						context.startActivity(gameActivityIntent)
					}
				})
				if (userSettings!!.optionalFeatures) {
					menuButton("VERSUS", onClick = {
						AudioManager.playSound(context, R.raw.click)
						context.startActivity(Intent(context, VersusActivity::class.java))
					})
				}
				menuButton("SCOREBOARD",onClick = {
					AudioManager.playSound(context, R.raw.click)
					context.startActivity(Intent(context, ScoreboardActivity::class.java))
				})
				menuButton("SETTINGS", onClick = {
					AudioManager.playSound(context, R.raw.click)
					context.startActivity(Intent(context, SettingsActivity::class.java))
				})

			}
			Column(modifier = Modifier.weight(0.5F)) { }

			if (showGridSizeDialog.value) {
				GridSizeDialog(context, gameActivityIntent)
			}
			if (showVersusDialog.value) {
				VersusDialog(context, gameActivityIntent)
			}
		}
	}


	@Composable
	private fun menuButton(text: String, onClick: () -> Unit) {
		Button(
			onClick = onClick,
			modifier = Modifier.padding(15.dp).fillMaxWidth().height(60.dp),
			colors = ButtonColors(
				MaterialTheme.colorScheme.primaryContainer,
				MaterialTheme.colorScheme.primary,
				MaterialTheme.colorScheme.errorContainer,
				MaterialTheme.colorScheme.error
			)
		){
			Text(text, fontFamily = blockyFont,
			fontWeight = FontWeight.Light, fontSize = 40.sp)
		}
	}

	@Composable
	private fun GridSizeDialog(context: Context, intent: Intent) {
		Dialog(
			onDismissRequest = {
				showGridSizeDialog.value = false
			}
		) {
			Card(
				modifier = Modifier
				.fillMaxWidth()
				.height(300.dp)
				.padding(12.dp),
				shape = RoundedCornerShape(16.dp)
			) {
				Column(
					modifier = Modifier.fillMaxSize(),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						"What grid size would you like to play ?",
						textAlign = TextAlign.Center
					)
					Spacer(modifier = Modifier.height(16.dp))
					GridSizeButton(3, context, intent)
					GridSizeButton(4, context, intent)
					GridSizeButton(5, context, intent)
					GridSizeButton(6, context, intent)
				}
			}
		}
	}

	@Composable
	private fun VersusDialog(context: Context, intent: Intent) {
		Dialog(
			onDismissRequest = {
				showVersusDialog.value = false
			}
		) {
			Card(
				modifier = Modifier
				.fillMaxWidth()
				.height(300.dp)
				.padding(12.dp),
				shape = RoundedCornerShape(16.dp)
			) {
				Column(
					modifier = Modifier.fillMaxSize(),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						"Time trial, or first to 2048 ?",
						textAlign = TextAlign.Center
					)
					Spacer(modifier = Modifier.height(16.dp))
					Row(
						horizontalArrangement = Arrangement.SpaceEvenly,
						modifier = Modifier.fillMaxWidth()
					) {
						Button(
							colors = buttonColors,
							onClick = {
								intent.putExtra("timer", true)
								showVersusDialog.value = false
								context.startActivity(intent)
							}
						) {
							Text("Time Trial")
						}
						Button(
							colors = buttonColors,
							onClick = {
								showVersusDialog.value = false
								context.startActivity(intent)
							}
						) {
							Text("2048")
						}
					}

				}
			}
		}
	}


	@Composable
	private fun GridSizeButton(size: Int, context: Context, intent: Intent) {
		Button(
			colors = buttonColors,
			onClick = {
				intent.putExtra("width", size)
				intent.putExtra("height", size)
				showGridSizeDialog.value = false
				context.startActivity(intent)
			}
		) {
			Text("${size}x${size}")
		}
	}
}
