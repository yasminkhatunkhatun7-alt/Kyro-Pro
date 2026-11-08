package com.kyro.app.core.agent.verification

import com.kyro.app.core.agent.planner.PlanStep
import com.kyro.app.core.agent.tools.ToolResult
import com.kyro.app.core.agent.planner.VerificationType

class DefaultTaskVerifier : TaskVerifier {
    override suspend fun verify(step: PlanStep, result: ToolResult): VerificationResult {
        if (result is ToolResult.Failure) {
            return VerificationResult.Failure("Tool execution failed.", result.isRecoverable)
        }

        val spec = step.verification ?: return VerificationResult.Success

        // Very basic verification logic based on output string matching
        val outputString = (result as? ToolResult.Success)?.output?.toString() ?: ""

        return when (spec.type) {
            VerificationType.NONE -> VerificationResult.Success
            VerificationType.EXACT_MATCH -> {
                if (outputString == spec.expectedOutput) VerificationResult.Success
                else VerificationResult.Failure("Expected exact match \${spec.expectedOutput} but got \$outputString", true)
            }
            VerificationType.CONTAINS -> {
                if (spec.expectedOutput != null && outputString.contains(spec.expectedOutput)) VerificationResult.Success
                else VerificationResult.Failure("Output did not contain \${spec.expectedOutput}", true)
            }
            VerificationType.AI_EVALUATION -> {
                VerificationResult.Failure("AI Evaluation not implemented.", false)
            }
        }
    }
}
