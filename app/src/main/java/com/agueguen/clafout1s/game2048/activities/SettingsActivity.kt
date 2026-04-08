package com.agueguen.clafout1s.game2048.activities

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.agueguen.clafout1s.game2048.AudioManager
import com.agueguen.clafout1s.game2048.R
import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme
import com.agueguen.clafout1s.game2048.ui.theme.lightScheme
import com.agueguen.clafout1s.game2048.ui.theme.darkScheme
import com.agueguen.clafout1s.game2048.database.UserSettings
import com.agueguen.clafout1s.game2048.database.UserSettingsDao

class SettingsActivity : AbstractActivity2048() {
	lateinit var userSettingsDao: UserSettingsDao

	@Composable
	override fun ScreenContent() {
		userSettingsDao = AppDatabase.getDatabase(this).userSettingsDao()
		val userSettingsState = remember { mutableStateOf(userSettingsDao.getUserSettings()) }
		val userSettings = userSettingsState.value!!
		val context = LocalContext.current

		val buttonModifiers = Modifier
		.padding(15.dp)
		.fillMaxWidth()
		.height(40.dp)

        val buttonColors = ButtonColors(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error)

		Column(
			modifier = Modifier
			.background(MaterialTheme.colorScheme.background)
			.fillMaxSize(),
			verticalArrangement = Arrangement.SpaceEvenly
		) {
			Text(
				text = "Settings",
				fontSize = 50.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier
				.padding(15.dp)
				.fillMaxWidth()
				.weight(1f)
				.wrapContentHeight(align = Alignment.CenterVertically),
				textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
			)

			Column(
				verticalArrangement = Arrangement.SpaceEvenly,
				modifier = Modifier.weight(2f)
			) {
				// Animations toggle
				Button(
                    colors = buttonColors,
					onClick = {
						AudioManager.playSound(context, R.raw.click)
						val updated = userSettings.copy(animations = !userSettings.animations)
						userSettingsState.value = updated
						userSettingsDao.insert(updated)
					},
					modifier = buttonModifiers
				) {
					Text(
						text = "Animations: ${if (userSettings.animations) "On" else "Off"}",
						fontWeight = FontWeight.Light,
						fontSize = 20.sp
					)
				}

				// Music toggle
				Button(
                    colors = buttonColors,
					onClick = {
						AudioManager.playSound(context, R.raw.click)
						val updated = userSettings.copy(music = !userSettings.music)
						userSettingsState.value = updated
						userSettingsDao.insert(updated)
					},
					modifier = buttonModifiers
				) {
					Text(
						text = "Music: ${if (userSettings.music) "On" else "Off"}",
						fontWeight = FontWeight.Light,
						fontSize = 20.sp
					)
				}

				// Sound FX toggle
				Button(
                    colors = buttonColors,
					onClick = {
						AudioManager.playSound(context, R.raw.click)
						val updated = userSettings.copy(soundFX = !userSettings.soundFX)
						userSettingsState.value = updated
						userSettingsDao.insert(updated)
					},
					modifier = buttonModifiers
				) {
					Text(
						text = "Sound FX: ${if (userSettings.soundFX) "On" else "Off"}",
						fontWeight = FontWeight.Light,
						fontSize = 20.sp
					)
				}

				// Optional features toggle
				Button(
                    colors = buttonColors,
					onClick = {
						AudioManager.playSound(context, R.raw.click)
						val updated = userSettings.copy(optionalFeatures = !userSettings.optionalFeatures)
						userSettingsState.value = updated
						userSettingsDao.insert(updated)
					},
					modifier = buttonModifiers
				) {
					Text(
						text = "Optional Features: ${if (userSettings.optionalFeatures) "On" else "Off"}",
						fontWeight = FontWeight.Light,
						fontSize = 20.sp
					)
				}

				ThemePicker(context, userSettingsState)
			}

			Column(modifier = Modifier.weight(0.5f)) {}
		}

	}

	@Composable
	fun ThemePicker(context: Context, userSettingsState: MutableState<UserSettings?>) {
		val userSettings = userSettingsState.value!!
		val outlineColor = MaterialTheme.colorScheme.onPrimary

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
						width = if (userSettings.theme == index) 3.dp else 0.dp,
						color = outlineColor,
						shape = CircleShape
					)
					.clickable {
						AudioManager.playSound(context, R.raw.click)
						val updated = userSettings.copy(theme = index)
						userSettingsState.value = updated
						userSettingsDao.insert(updated)
					},
					contentAlignment = Alignment.Center
				) {
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

