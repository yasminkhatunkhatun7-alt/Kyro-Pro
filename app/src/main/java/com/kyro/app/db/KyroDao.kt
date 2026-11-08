package com.kyro.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kyro.app.core.agent.state.TaskStatus

@Dao
interface KyroDao {
    @Query("SELECT * FROM macros WHERE triggerPhrase = :phrase LIMIT 1")
    suspend fun getMacroByTrigger(phrase: String): Macro?

    @Insert
    suspend fun insertMacro(macro: Macro)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AgentTaskEntity)

    @Query("UPDATE agent_tasks SET status = :status, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM agent_tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTask(taskId: String): AgentTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: AgentStepEntity)
}
