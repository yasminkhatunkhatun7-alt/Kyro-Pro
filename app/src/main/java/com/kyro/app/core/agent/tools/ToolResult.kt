package com.kyro.app.core.agent.tools

import kotlinx.serialization.json.JsonElement

sealed class ToolResult {
    data class Success(
        val output: JsonElement,
        val message: String? = null
    ) : ToolResult()

    data class Failure(
        val error: String,
        val isRecoverable: Boolean = false
    ) : ToolResult()
}
