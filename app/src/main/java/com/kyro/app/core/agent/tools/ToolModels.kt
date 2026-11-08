package com.kyro.app.core.agent.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.booleanOrNull

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
    val inputSchema: String
)

data class ToolInput(
    val arguments: JsonObject
) {
    fun getString(key: String): String? = arguments[key]?.jsonPrimitive?.content
    fun getBoolean(key: String): Boolean? = arguments[key]?.jsonPrimitive?.booleanOrNull
}

class ToolExecutionContext(
    val taskId: String,
    val context: Map<String, Any> = emptyMap()
)
