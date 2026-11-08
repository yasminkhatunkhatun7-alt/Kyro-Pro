package com.kyro.app.core.agent.tools

interface AgentTool {
    val id: String
    val name: String
    val description: String
    // Simplified schema representation for now
    val inputSchema: String
    val metadata: ToolMetadata

    suspend fun execute(
        input: ToolInput,
        context: ToolExecutionContext
    ): ToolResult
}
