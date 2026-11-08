package com.kyro.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Macro::class, AgentTaskEntity::class, AgentStepEntity::class], version = 2, exportSchema = false)
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
                )
                .fallbackToDestructiveMigration() // Allowed during early alpha, real prod needs Migration strategies
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
