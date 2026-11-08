package com.kyro.app.core.agent.api

data class AgentConfig(
    val maxSteps: Int = 12,
    val maxRetriesPerStep: Int = 2,
    val requireApprovalForRiskyActions: Boolean = true,
    val allowParallelReadOnlyTools: Boolean = true,
    val persistTask: Boolean = true
)
