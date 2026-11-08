package com.kyro.app.core.agent.execution

import com.kyro.app.core.agent.api.AgentResult
import com.kyro.app.core.agent.planner.AgentPlan
import com.kyro.app.core.agent.tools.ToolRegistry
import com.kyro.app.core.agent.policy.PolicyEngine
import com.kyro.app.core.agent.policy.PolicyDecision
import com.kyro.app.core.agent.tools.ToolInput
import com.kyro.app.core.agent.tools.ToolExecutionContext
import com.kyro.app.core.agent.tools.ToolResult

class DefaultAgentExecutor(
    private val toolRegistry: ToolRegistry,
    private val policyEngine: PolicyEngine
) : AgentExecutor {

    override suspend fun executePlan(plan: AgentPlan): AgentResult {
        var completedSteps = 0

        for (step in plan.steps.sortedBy { it.order }) {
            val tool = toolRegistry.get(step.toolId)
                ?: return AgentResult.Failure(
                    taskId = plan.taskId,
                    message = "Tool not found: \${step.toolId}",
                    completedSteps = completedSteps,
                    totalSteps = plan.steps.size,
                    recoverable = false
                )

            val decision = policyEngine.evaluate(tool)
            if (decision is PolicyDecision.Denied) {
                return AgentResult.Failure(
                    taskId = plan.taskId,
                    message = "Execution denied: \${decision.reason}",
                    completedSteps = completedSteps,
                    totalSteps = plan.steps.size,
                    recoverable = false
                )
            }
            if (decision is PolicyDecision.RequiresApproval) {
                // In a full implementation, pause execution and wait for user approval UI event
                // For now, fail execution if approval is required.
                return AgentResult.Failure(
                    taskId = plan.taskId,
                    message = "Requires approval: \${decision.reason}",
                    completedSteps = completedSteps,
                    totalSteps = plan.steps.size,
                    recoverable = true
                )
            }

            val context = ToolExecutionContext(taskId = plan.taskId)
            val result = tool.execute(ToolInput(step.arguments), context)

            if (result is ToolResult.Failure) {
                return AgentResult.Failure(
                    taskId = plan.taskId,
                    message = "Step \${step.id} failed: \${result.error}",
                    completedSteps = completedSteps,
                    totalSteps = plan.steps.size,
                    recoverable = result.isRecoverable
                )
            }
            completedSteps++
        }

        return AgentResult.Success(
            taskId = plan.taskId,
            message = "Plan completed successfully.",
            completedSteps = completedSteps,
            totalSteps = plan.steps.size
        )
    }
}
