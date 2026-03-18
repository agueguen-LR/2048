package com.agueguen.clafout1s.game2048.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SaveState::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
	abstract fun saveStateDao(): SaveStateDao

	companion object {
		private var INSTANCE: AppDatabase? = null

		fun getDatabase(context: Context): AppDatabase {
			if (INSTANCE == null) {
				synchronized(this) {
					INSTANCE = Room.databaseBuilder(
						context.applicationContext,
						AppDatabase::class.java,
						"app_database"
					).allowMainThreadQueries().build()
				}
			}
			return INSTANCE!!
		}
	}

}
