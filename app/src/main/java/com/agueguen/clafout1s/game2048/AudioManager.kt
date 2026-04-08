package com.agueguen.clafout1s.game2048

import android.media.MediaPlayer
import android.content.Context
import android.media.SoundPool
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.MutableState

import com.agueguen.clafout1s.game2048.database.UserSettings

object AudioManager {

	private var musicPlayer: MediaPlayer? = null
	var isInitialized = false

	private lateinit var userSettings: UserSettings

	fun init(context: Context, settings: UserSettings?) {
		userSettings = settings ?: UserSettings()

		if (userSettings.music) {
			playMusic(context, R.raw.bg_music)
		} else {
			stopMusic()
		}

		isInitialized = true
	}

	fun playMusic(context: Context, resId: Int) {
		if (userSettings.music != true) return

		if (musicPlayer == null) {
			musicPlayer = MediaPlayer.create(context.applicationContext, resId).apply {
				isLooping = true
			}
		}
		musicPlayer?.start()
	}

	fun pauseMusic() {
		musicPlayer?.pause()
	}

	fun stopMusic() {
		musicPlayer?.release()
		musicPlayer = null
	}

	fun playSound(context: Context, resId: Int) {
		if (userSettings.soundFX != true) return
		val sound = MediaPlayer.create(context, resId)
		sound.setOnCompletionListener {
			it.release()
		}
		sound.start()
	}
}
