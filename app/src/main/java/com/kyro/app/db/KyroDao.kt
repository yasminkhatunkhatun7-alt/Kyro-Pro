package com.kyro.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface KyroDao {
    @Query("SELECT * FROM macros WHERE triggerPhrase = :phrase LIMIT 1")
    suspend fun getMacroByTrigger(phrase: String): Macro?

    @Insert
    suspend fun insertMacro(macro: Macro)
}
