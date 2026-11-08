package com.kyro.app.core.agent.planner

import com.kyro.app.ai.AIProvider
import com.kyro.app.ai.AIResponse
import com.kyro.app.core.agent.api.AgentRequest
import com.kyro.app.core.agent.tools.ToolRegistry
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class DefaultTaskPlanner(
    private val aiProvider: AIProvider,
    private val toolRegistry: ToolRegistry,
    private val validator: PlanValidator,
    private val dependencyResolver: DependencyResolver
) : TaskPlanner {

    override suspend fun plan(request: AgentRequest): AgentPlan? {
        val toolsDesc = toolRegistry.getDescriptions().joinToString("\n") { it.name + " (" + it.id + "): " + it.description }
        val prompt = "Create a plan for goal: \${request.userGoal}\nAvailable tools:\n\$toolsDesc\nProvide the response as a JSON array of step objects, each with id, toolId, and arguments."

        val response = aiProvider.generateResponse(prompt)
        if (response is AIResponse.Success) {
            // Simplified fallback for demo/skeleton purposes.
            // A real implementation would parse the JSON correctly using kotlinx.serialization
            val fakePlan = buildFallbackPlan(request)
            if (validator.validate(fakePlan)) {
                return fakePlan.copy(steps = dependencyResolver.resolveExecutionOrder(fakePlan.steps))
            }
        }
        return null
    }

    private fun buildFallbackPlan(request: AgentRequest): AgentPlan {
        val lowerGoal = request.userGoal.lowercase()
        val steps = mutableListOf<PlanStep>()
        if (lowerGoal.contains("flashlight")) {
            val isOn = !lowerGoal.contains("off")
            steps.add(PlanStep("step1", 1, "Toggle flashlight", "Turn flashlight on/off", "android.flashlight", JsonObject(mapOf("on" to JsonPrimitive(isOn)))))
        } else if (lowerGoal.contains("open")) {
            val app = lowerGoal.replace("open", "").trim()
            steps.add(PlanStep("step1", 1, "Open App", "Opens an app", "android.open_app", JsonObject(mapOf("app_name" to JsonPrimitive(app)))))
        }

        return AgentPlan(request.id, request.userGoal, "Generated plan", steps)
    }
}
