package com.kyro.app.core.agent.policy

import com.kyro.app.core.agent.tools.AgentTool

interface PolicyEngine {
    fun evaluate(tool: AgentTool): PolicyDecision
}

sealed class PolicyDecision {
    object Allowed : PolicyDecision()
    data class RequiresApproval(val reason: String) : PolicyDecision()
    data class Denied(val reason: String) : PolicyDecision()
}
