package com.kyro.app.core.agent.tools.impl

import com.kyro.app.action.ActionExecutor
import com.kyro.app.core.agent.tools.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

class AndroidOpenAppTool(private val actionExecutor: ActionExecutor) : AgentTool {
    override val id = "android.open_app"
    override val name = "Open Application"
    override val description = "Opens an installed Android application by name."
    override val inputSchema = "{ \"type\": \"object\", \"properties\": { \"app_name\": { \"type\": \"string\" } }, \"required\": [\"app_name\"] }"
    override val metadata = ToolMetadata(
        riskLevel = ToolRisk.LOW,
        readOnly = false
    )

    override suspend fun execute(input: ToolInput, context: ToolExecutionContext): ToolResult {
        val appName = input.arguments["app_name"]?.jsonPrimitive?.content
            ?: return ToolResult.Failure("Missing required argument 'app_name'")

        return try {
            val resultMessage = actionExecutor.openApp(appName)
            if (resultMessage.startsWith("Opening")) {
                ToolResult.Success(JsonPrimitive(resultMessage), resultMessage)
            } else {
                ToolResult.Failure(resultMessage, isRecoverable = true)
            }
        } catch (e: Exception) {
            ToolResult.Failure("Failed to open app: \${e.message}")
        }
    }
}
