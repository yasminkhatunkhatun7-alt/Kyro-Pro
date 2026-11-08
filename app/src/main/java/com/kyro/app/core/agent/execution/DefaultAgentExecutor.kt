package com.kyro.app.core.agent.execution

import com.kyro.app.core.agent.api.AgentResult
import com.kyro.app.core.agent.planner.AgentPlan
import com.kyro.app.core.agent.tools.ToolRegistry
import com.kyro.app.core.agent.policy.PolicyEngine
import com.kyro.app.core.agent.policy.PolicyDecision
import com.kyro.app.core.agent.tools.ToolInput
import com.kyro.app.core.agent.tools.ToolExecutionContext
import com.kyro.app.core.agent.tools.ToolResult
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

class DefaultAgentExecutor(
    private val toolRegistry: ToolRegistry,
    private val policyEngine: PolicyEngine
) : AgentExecutor {

    override suspend fun executePlan(plan: AgentPlan): AgentResult {
        var completedSteps = 0
        val successfulStepIds = mutableSetOf<String>()

        for (step in plan.steps) {
            if (!coroutineContext.isActive) {
                return AgentResult.Cancelled(plan.taskId, "Execution cancelled.", completedSteps, plan.steps.size)
            }

            // Check dependencies
            if (!successfulStepIds.containsAll(step.dependsOn)) {
                return AgentResult.Failure(plan.taskId, "Dependencies for step \${step.id} were not met.", completedSteps, plan.steps.size, false)
            }

            val tool = toolRegistry.get(step.toolId)
                ?: return AgentResult.Failure(plan.taskId, "Tool not found: \${step.toolId}", completedSteps, plan.steps.size, false)

            val decision = policyEngine.evaluate(tool)
            if (decision is PolicyDecision.Denied) {
                return AgentResult.Failure(plan.taskId, "Execution denied: \${decision.reason}", completedSteps, plan.steps.size, false)
            }

            if (decision is PolicyDecision.RequiresApproval) {
                return AgentResult.Failure(plan.taskId, "Requires approval: \${decision.reason}", completedSteps, plan.steps.size, true)
            }

            val context = ToolExecutionContext(taskId = plan.taskId)

            try {
                val result = withTimeout(tool.metadata.timeoutMillis) {
                    tool.execute(ToolInput(step.arguments), context)
                }

                if (result is ToolResult.Failure) {
                    return AgentResult.Failure(plan.taskId, "Step \${step.id} failed: \${result.error}", completedSteps, plan.steps.size, result.isRecoverable)
                }
                successfulStepIds.add(step.id)
                completedSteps++
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                 return AgentResult.Failure(plan.taskId, "Step \${step.id} timed out.", completedSteps, plan.steps.size, true)
            } catch (e: Exception) {
                 return AgentResult.Failure(plan.taskId, "Step \${step.id} threw an exception: \${e.message}", completedSteps, plan.steps.size, false)
            }
        }

        return AgentResult.Success(plan.taskId, "Plan completed successfully.", completedSteps, plan.steps.size)
    }
}
