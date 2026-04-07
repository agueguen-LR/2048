package com.agueguen.clafout1s.game2048.activities

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont

class MainMenuActivity : AbstractActivity2048() {
	private lateinit var showGridSizeDialog: MutableState<Boolean>

	@Composable
	override fun ScreenContent(){
		showGridSizeDialog = remember { mutableStateOf(false) }
		val commonModifierBases = Modifier.padding(15.dp).fillMaxWidth()
		val buttonModifiers = commonModifierBases.height(80.dp)
		val context = LocalContext.current
		val gameActivityIntent = Intent(context, GameActivity::class.java)
		val buttonColors = ButtonColors(
			MaterialTheme.colorScheme.surfaceBright,
			MaterialTheme.colorScheme.primary,
			MaterialTheme.colorScheme.errorContainer,
			MaterialTheme.colorScheme.error)
		val userSettings = AppDatabase.getDatabase(context).userSettingsDao().getUserSettings()

		Column (
			modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxSize(),
			verticalArrangement = Arrangement.SpaceEvenly
		) {
			Text(
				text = "2048",
				fontSize = 60.sp,
				fontFamily = blockyFont,
				fontWeight = FontWeight.Bold,
				style = TextStyle(
					brush = Brush.linearGradient(
						listOf(MaterialTheme.colorScheme.primary ,MaterialTheme.colorScheme.inversePrimary)
					)
				),
				modifier = commonModifierBases.weight(1F).wrapContentHeight(align= Alignment.CenterVertically),
				textAlign = TextAlign.Center,
			)
			Column(verticalArrangement = Arrangement.SpaceEvenly,
				modifier = Modifier.weight(2F)
			) {
				Button(
					onClick = { 
						if (userSettings!!.optionalFeatures) {
							showGridSizeDialog.value = true
						} else context.startActivity(gameActivityIntent)
					},
					modifier = buttonModifiers,
					colors = buttonColors
				) {
					Text("START", fontFamily = blockyFont,
						fontWeight = FontWeight.Light, fontSize = 40.sp)
				}
				Button(
					onClick = { context.startActivity(Intent(context, ScoreboardActivity::class.java))},
					modifier = buttonModifiers,
					colors = buttonColors
				){
					Text("SCOREBOARD", fontFamily = blockyFont,
						fontWeight = FontWeight.Light, fontSize = 40.sp)
				}
				Button(
					onClick = { context.startActivity(Intent(context, SettingsActivity::class.java))},
					modifier = buttonModifiers,
					colors = buttonColors
				){
					Text("SETTINGS", fontFamily = blockyFont,
						fontWeight = FontWeight.Light, fontSize = 40.sp)
				}
			}
			Column(modifier = Modifier.weight(0.5F)) { }
		}

		if (showGridSizeDialog.value) gridSizeDialog(context, gameActivityIntent)
	}

	@Composable
	private fun gridSizeDialog(context: Context, intent: Intent) {
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
				shape = RoundedCornerShape(16.dp),
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
					gridSizeButton(3, context, intent)
					gridSizeButton(4, context, intent)
					gridSizeButton(5, context, intent)
					gridSizeButton(6, context, intent)
				}
			}
		}
	}

	@Composable
	private fun gridSizeButton(size: Int, context: Context, intent: Intent) {
		Button(onClick = {
			intent.putExtra("width", size)
			intent.putExtra("height", size)
			showGridSizeDialog.value = false
			context.startActivity(intent)
		}) {
			Text("${size}x${size}")
		}
	}
}
