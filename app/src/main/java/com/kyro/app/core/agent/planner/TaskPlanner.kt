package com.kyro.app.core.agent.planner

import com.kyro.app.core.agent.api.AgentRequest

interface TaskPlanner {
    suspend fun plan(request: AgentRequest): AgentPlan?
}
