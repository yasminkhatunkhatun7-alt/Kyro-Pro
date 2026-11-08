package com.kyro.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Macro::class], version = 1, exportSchema = false)
abstract class KyroDatabase : RoomDatabase() {
    abstract fun kyroDao(): KyroDao

    companion object {
        @Volatile
        private var INSTANCE: KyroDatabase? = null

        fun getDatabase(context: Context): KyroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KyroDatabase::class.java,
                    "kyro_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
