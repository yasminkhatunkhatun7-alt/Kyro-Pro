package com.kyro.app.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kyro.app.core.agent.state.TaskStatus

@Entity(tableName = "agent_tasks")
data class AgentTaskEntity(
    @PrimaryKey val id: String,
    val goal: String,
    val status: TaskStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "agent_steps")
data class AgentStepEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val toolId: String,
    val status: String,
    val result: String? = null
)
