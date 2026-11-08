package com.kyro.app.core.agent.state

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface TaskStateManager {
    val events: SharedFlow<AgentEvent>
    suspend fun emitEvent(event: AgentEvent)
    fun getStatus(taskId: String): TaskStatus?
    fun updateStatus(taskId: String, status: TaskStatus)
}

class DefaultTaskStateManager : TaskStateManager {
    private val _events = MutableSharedFlow<AgentEvent>(replay = 100, extraBufferCapacity = 100)
    override val events: SharedFlow<AgentEvent> = _events.asSharedFlow()

    private val statuses = mutableMapOf<String, TaskStatus>()

    override suspend fun emitEvent(event: AgentEvent) {
        _events.emit(event)
    }

    override fun getStatus(taskId: String): TaskStatus? = statuses[taskId]

    override fun updateStatus(taskId: String, status: TaskStatus) {
        statuses[taskId] = status
    }
}
