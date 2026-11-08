package com.kyro.app.core.agent.api

interface AgentEngine {
    suspend fun execute(request: AgentRequest): AgentResult
    fun cancel(taskId: String)
}
