package com.agueguen.clafout1s.game2048

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme
import com.agueguen.clafout1s.game2048.ui.theme.lightScheme
import com.agueguen.clafout1s.game2048.ui.theme.darkScheme
import com.agueguen.clafout1s.game2048.database.UserSettings
import com.agueguen.clafout1s.game2048.database.UserSettingsDao

class SettingsActivity : ComponentActivity() {
	private lateinit var userSettingsDao : UserSettingsDao
	private lateinit var theme: MutableState<Int>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		userSettingsDao = AppDatabase.getDatabase(this).userSettingsDao()
		setContent {
			theme = remember { mutableStateOf(userSettingsDao.getUserSettings().theme) }
			AppTheme(theme.value) {
				Scaffold(modifier = Modifier.fillMaxSize()) {
					Settings()
				}
			}
		}
	}

	@Composable
	fun Settings() {
		var userSettings = remember { mutableStateOf(userSettingsDao.getUserSettings()) }
		val buttonModifiers = Modifier.padding(15.dp).fillMaxWidth().height(40.dp)

		Column(
			modifier = Modifier
			.background(MaterialTheme.colorScheme.surface)
			.fillMaxSize(),
			verticalArrangement = Arrangement.SpaceEvenly
		) {
			Text(
				text = "Settings",
				fontSize = 50.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(15.dp).fillMaxWidth()
				.weight(1f)
				.wrapContentHeight(align = Alignment.CenterVertically),
				textAlign = TextAlign.Center
			)

			Column(
				verticalArrangement = Arrangement.SpaceEvenly,
				modifier = Modifier.weight(2f)
			) {
				// Animations toggle
				Button(
					onClick = {
						val updated = userSettings.value.copy(animations = !userSettings.value.animations)
						userSettings.value = updated
						userSettingsDao.insert(updated)
					},
					modifier = buttonModifiers
				) {
					Text(
						text = "Animations: ${if (userSettings.value.animations) "On" else "Off"}",
						fontWeight = FontWeight.Light,
						fontSize = 20.sp
					)
				}

				// Music toggle
				Button(
					onClick = {
						val updated = userSettings.value.copy(music = !userSettings.value.music)
						userSettings.value = updated
						userSettingsDao.insert(updated)
					},
					modifier = buttonModifiers
				) {
					Text(
						text = "Music: ${if (userSettings.value.music) "On" else "Off"}",
						fontWeight = FontWeight.Light,
						fontSize = 20.sp
					)
				}

				// SoundFX toggle
				Button(
					onClick = {
						val updated = userSettings.value.copy(soundFX = !userSettings.value.soundFX)
						userSettings.value = updated
						userSettingsDao.insert(updated)
					},
					modifier = buttonModifiers
				) {
					Text(
						text = "Sound FX: ${if (userSettings.value.soundFX) "On" else "Off"}",
						fontWeight = FontWeight.Light,
						fontSize = 20.sp
					)
				}

				// Optional features toggle
				Button(
					onClick = {
						val updated = userSettings.value.copy(optionalFeatures = !userSettings.value.optionalFeatures)
						userSettings.value = updated
						userSettingsDao.insert(updated)
					},
					modifier = buttonModifiers
				) {
					Text(
						text = "Optional Features: ${if (userSettings.value.optionalFeatures) "On" else "Off"}",
						fontWeight = FontWeight.Light,
						fontSize = 20.sp
					)
				}

				ThemePicker(userSettings)
			}

			Column(modifier = Modifier.weight(0.5f)) {}
		}
	}

	@Composable
	fun ThemePicker(userSettings: MutableState<UserSettings>) {
		val outlineColor = MaterialTheme.colorScheme.secondary // visible on any background

		// List of themes: Pair<primaryColor, backgroundColor>
		val themes = listOf(
			lightScheme.primary to lightScheme.background,
			darkScheme.primary to darkScheme.background
			// Add more themes if needed
		)

		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier.padding(16.dp)
		) {
			themes.forEachIndexed { index, (primary, background) ->

				Box(
					modifier = Modifier
					.size(50.dp)
					.background(background, CircleShape)
					.border(
						width = if (userSettings.value.theme == index) 3.dp else 0.dp,
						color = outlineColor,
						shape = CircleShape
					)
					.clickable {
						val updated = userSettings.value.copy(theme = index)
						userSettings.value = updated
						theme.value = index
						userSettingsDao.insert(updated)
					},
					contentAlignment = Alignment.Center
				) {
					// Inner circle
					Box(
						modifier = Modifier
						.size(25.dp)
						.background(primary, CircleShape)
					)
				}
			}
		}
	}
}

