package com.agueguen.clafout1s.game2048

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont
import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.database.UserSettings

class MainMenuActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val userSettingsDao = AppDatabase.getDatabase(this).userSettingsDao()
		var settings = userSettingsDao.getUserSettings()
		if (settings == null) {
			settings = UserSettings()
			userSettingsDao.insert(settings)
		}
		setContent {
			AppTheme(settings.theme) {
				Scaffold(modifier = Modifier.fillMaxSize()) {
					Menu()
				}
			}
		}

	}

	// TODO: Change the connexions of the buttons to their actual activities
	@Composable
	private fun Menu() {
		val commonModifierBases = Modifier.padding(15.dp).fillMaxWidth()
		val buttonModifiers = commonModifierBases.height(80.dp)
		val context = LocalContext.current
		val buttonColors = ButtonColors(
			MaterialTheme.colorScheme.surfaceBright,
			MaterialTheme.colorScheme.primary,
			MaterialTheme.colorScheme.errorContainer,
			MaterialTheme.colorScheme.error)
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
						brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary ,MaterialTheme.colorScheme.inversePrimary))
					),
					modifier = commonModifierBases.weight(1F).wrapContentHeight(align= Alignment.CenterVertically),
					textAlign = TextAlign.Center,
				)
				Column(verticalArrangement = Arrangement.SpaceEvenly,
				modifier = Modifier.weight(2F)
			) {
				Button(
					onClick = { context.startActivity(Intent(context, SwapTestActivity::class.java)) },
					modifier = buttonModifiers,
					colors = buttonColors
				) {
					Text("START", fontFamily = blockyFont, fontWeight = FontWeight.Light, fontSize = 40.sp)
				}
				Button(
					onClick = { context.startActivity(Intent(context, SwapTestActivity::class.java))},
					modifier = buttonModifiers,
					colors = buttonColors
				){
					Text("SCOREBOARD",fontFamily = blockyFont, fontWeight = FontWeight.Light, fontSize = 40.sp)
				}
				Button(
					onClick = { context.startActivity(Intent(context, SettingsActivity::class.java))},
					modifier = buttonModifiers,
					colors = buttonColors
				){
					Text("SETTINGS",fontFamily = blockyFont, fontWeight = FontWeight.Light, fontSize = 40.sp)
				}
			}
			Column(modifier = Modifier.weight(0.5F)) { }

		}
	}
}

