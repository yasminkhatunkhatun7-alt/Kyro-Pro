package com.kyro.app.core.agent.planner

import kotlinx.serialization.json.JsonObject

data class AgentPlan(
    val taskId: String,
    val goal: String,
    val summary: String,
    val steps: List<PlanStep>,
    val requiresApproval: Boolean = false
)

data class PlanStep(
    val id: String,
    val order: Int,
    val title: String,
    val description: String,
    val toolId: String,
    val arguments: JsonObject,
    val dependsOn: List<String> = emptyList(),
    val requiresApproval: Boolean = false,
    val verification: VerificationSpec? = null
)

data class VerificationSpec(
    val type: VerificationType,
    val expectedOutput: String? = null
)

enum class VerificationType {
    EXACT_MATCH,
    CONTAINS,
    AI_EVALUATION,
    NONE
}
