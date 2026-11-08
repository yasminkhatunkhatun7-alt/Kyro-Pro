package com.kyro.app.core.agent.verification

import com.kyro.app.core.agent.planner.PlanStep
import com.kyro.app.core.agent.tools.ToolResult

interface TaskVerifier {
    suspend fun verify(step: PlanStep, result: ToolResult): VerificationResult
}

sealed class VerificationResult {
    object Success : VerificationResult()
    data class Failure(val reason: String, val recoverable: Boolean) : VerificationResult()
}
