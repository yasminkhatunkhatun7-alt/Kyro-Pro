package com.kyro.app.core.agent.planner

import com.kyro.app.ai.AIProvider
import com.kyro.app.ai.AIResponse
import com.kyro.app.core.agent.api.AgentRequest
import com.kyro.app.core.agent.tools.ToolRegistry
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.booleanOrNull

class DefaultTaskPlanner(
    private val aiProvider: AIProvider,
    private val toolRegistry: ToolRegistry,
    private val validator: PlanValidator,
    private val dependencyResolver: DependencyResolver
) : TaskPlanner {

    private val jsonParser = Json { ignoreUnknownKeys = true }

    override suspend fun plan(request: AgentRequest): AgentPlan? {
        val toolsDesc = toolRegistry.getDescriptions().joinToString("\n") { it.name + " (" + it.id + "): " + it.description }
        val prompt = """
            Create a plan for goal: ${request.userGoal}
            Available tools:
            $toolsDesc
            Return ONLY a valid JSON object matching this schema:
            {
              "summary": "String",
              "steps": [
                {
                  "id": "String",
                  "order": Int,
                  "title": "String",
                  "description": "String",
                  "toolId": "String",
                  "arguments": { "key": "value" },
                  "dependsOn": ["String"]
                }
              ]
            }
        """.trimIndent()

        val response = aiProvider.generateResponse(prompt)
        if (response is AIResponse.Success) {
            try {
                // Strip markdown formatting if AI wraps it in ```json ... ```
                var cleanJson = response.text.trim()
                if (cleanJson.startsWith("```json")) {
                    cleanJson = cleanJson.substring(7)
                }
                if (cleanJson.endsWith("```")) {
                    cleanJson = cleanJson.substring(0, cleanJson.length - 3)
                }

                val jsonElement = jsonParser.parseToJsonElement(cleanJson).jsonObject
                val summary = jsonElement["summary"]?.jsonPrimitive?.content ?: "Generated plan"

                val stepsArray = jsonElement["steps"]?.jsonArray ?: return null
                val planSteps = stepsArray.map { stepElement ->
                    val stepObj = stepElement.jsonObject
                    PlanStep(
                        id = stepObj["id"]?.jsonPrimitive?.content ?: java.util.UUID.randomUUID().toString(),
                        order = stepObj["order"]?.jsonPrimitive?.intOrNull ?: 0,
                        title = stepObj["title"]?.jsonPrimitive?.content ?: "",
                        description = stepObj["description"]?.jsonPrimitive?.content ?: "",
                        toolId = stepObj["toolId"]?.jsonPrimitive?.content ?: "",
                        arguments = stepObj["arguments"]?.jsonObject ?: JsonObject(emptyMap()),
                        dependsOn = stepObj["dependsOn"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
                    )
                }

                val plan = AgentPlan(request.id, request.userGoal, summary, planSteps)

                if (validator.validate(plan)) {
                    val resolvedSteps = dependencyResolver.resolveExecutionOrder(plan.steps)
                    return plan.copy(steps = resolvedSteps)
                }
            } catch (e: Exception) {
                // Parsing failed
                return null
            }
        }
        return null
    }
}
