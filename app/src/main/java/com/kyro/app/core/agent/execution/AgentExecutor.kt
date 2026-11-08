package com.kyro.app.core.agent.execution

import com.kyro.app.core.agent.planner.AgentPlan
import com.kyro.app.core.agent.api.AgentResult

interface AgentExecutor {
    suspend fun executePlan(plan: AgentPlan): AgentResult
}
