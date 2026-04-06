package com.agueguen.clafout1s.game2048

import android.media.MediaPlayer
import android.content.Context
import android.media.SoundPool
import androidx.compose.runtime.MutableState

import com.agueguen.clafout1s.game2048.database.UserSettings

object AudioManager {

	private var musicPlayer: MediaPlayer? = null
	private var soundPool: SoundPool? = null
	private val soundMap = mutableMapOf<Int, Int>()

	private lateinit var userSettings: UserSettings

	fun init(context: Context, settings: UserSettings?) {
		userSettings = settings ?: UserSettings()

		if (soundPool == null) {
			soundPool = SoundPool.Builder()
			.setMaxStreams(5)
			.build()

			// preload sounds
			// soundMap[R.raw.click] = soundPool!!.load(context, R.raw.click, 1)
			// soundMap[R.raw.explosion] = soundPool!!.load(context, R.raw.explosion, 1)
		}

		if (userSettings.music) {
			playMusic(context, R.raw.bg_music)
		} else {
			stopMusic()
		}
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

	fun playSound(resId: Int, volume: Float = 1f) {
		if (userSettings.soundFX != true) return

		val soundId = soundMap[resId] ?: return
		soundPool?.play(soundId, volume, volume, 1, 0, 1f)
	}
}
