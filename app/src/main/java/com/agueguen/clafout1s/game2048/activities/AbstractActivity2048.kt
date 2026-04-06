package com.agueguen.clafout1s.game2048.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow

import com.agueguen.clafout1s.game2048.R
import com.agueguen.clafout1s.game2048.AudioManager
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme
import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.database.UserSettingsDao
import com.agueguen.clafout1s.game2048.database.UserSettings

abstract class AbstractActivity2048(private val modifier: Modifier = Modifier.fillMaxSize()) : ComponentActivity() {

	@Composable
	abstract fun ScreenContent()

	protected override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val dao = AppDatabase.getDatabase(this).userSettingsDao()

		setContent {
			val context = LocalContext.current
			val lifecycleOwner = LocalLifecycleOwner.current
			val settings by dao.getUserSettingsFlow().collectAsState(initial = UserSettings())

			if (dao.getUserSettings() == null) dao.insert(UserSettings())

			AudioManager.init(context, settings)

			DisposableEffect(lifecycleOwner) {
				val observer = LifecycleEventObserver { _, event ->
					when (event) {
						Lifecycle.Event.ON_PAUSE -> AudioManager.pauseMusic()
						Lifecycle.Event.ON_RESUME -> AudioManager.playMusic(context, R.raw.bg_music)
						else -> {}
					}
				}
				lifecycleOwner.lifecycle.addObserver(observer)
				onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
			}

			AppTheme(settings?.theme) {
				Scaffold(modifier = modifier) {
					ScreenContent()
				}
			}
		}
	}
}
