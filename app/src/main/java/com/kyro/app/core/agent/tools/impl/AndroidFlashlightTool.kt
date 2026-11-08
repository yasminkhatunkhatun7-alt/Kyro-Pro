package com.kyro.app.core.agent.tools.impl

import com.kyro.app.action.ActionExecutor
import com.kyro.app.core.agent.tools.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive

class AndroidFlashlightTool(private val actionExecutor: ActionExecutor) : AgentTool {
    override val id = "android.flashlight"
    override val name = "Toggle Flashlight"
    override val description = "Turns the device flashlight on or off."
    override val inputSchema = "{ \"type\": \"object\", \"properties\": { \"on\": { \"type\": \"boolean\" } }, \"required\": [\"on\"] }"
    override val metadata = ToolMetadata(
        riskLevel = ToolRisk.LOW,
        requiredPermissions = setOf("android.permission.CAMERA"),
        readOnly = false
    )

    override suspend fun execute(input: ToolInput, context: ToolExecutionContext): ToolResult {
        val turnOn = input.arguments["on"]?.jsonPrimitive?.booleanOrNull
            ?: return ToolResult.Failure("Missing required boolean argument 'on'")

        return try {
            val resultMessage = actionExecutor.toggleFlashlight(turnOn)
            if (resultMessage.startsWith("Turning")) {
                ToolResult.Success(JsonPrimitive(resultMessage), resultMessage)
            } else {
                ToolResult.Failure(resultMessage, isRecoverable = false)
            }
        } catch (e: Exception) {
            ToolResult.Failure("Failed to toggle flashlight: \${e.message}")
        }
    }
}
