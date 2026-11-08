package com.kyro.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `agent_tasks` (`id` TEXT NOT NULL, `goal` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        database.execSQL("CREATE TABLE IF NOT EXISTS `agent_steps` (`id` TEXT NOT NULL, `taskId` TEXT NOT NULL, `toolId` TEXT NOT NULL, `status` TEXT NOT NULL, `result` TEXT, PRIMARY KEY(`id`))")
    }
}

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
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
