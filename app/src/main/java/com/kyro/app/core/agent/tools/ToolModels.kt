package com.kyro.app.core.agent.tools

import kotlinx.serialization.json.JsonObject

enum class ToolRisk {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class ToolMetadata(
    val riskLevel: ToolRisk,
    val requiredPermissions: Set<String> = emptySet(),
    val requiresApproval: Boolean = false,
    val readOnly: Boolean = false,
    val timeoutMillis: Long = 30_000
)

data class ToolDescription(
    val id: String,
    val name: String,
    val description: String,
    // Using string representation of schema for simplicity, normally JsonObject or specific schema class
    val inputSchema: String
)

data class ToolInput(
    val arguments: JsonObject
)

class ToolExecutionContext(
    val taskId: String,
    val context: Map<String, Any> = emptyMap()
)
