package com.agueguen.clafout1s.game2048.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [], version = 1)
abstract class Database : RoomDatabase() {
  companion object {
    private var INSTANCE: Database? = null
    fun getDatabase(context: Context): Database {
      if (INSTANCE == null) {
        synchronized(this) {
          INSTANCE = Room.databaseBuilder(context, Database::class.java, "contact_database").build()
        }
      }
      return INSTANCE!!
    }
  }
}
