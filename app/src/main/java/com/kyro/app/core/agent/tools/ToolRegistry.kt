package com.kyro.app.core.agent.tools

interface ToolRegistry {
    fun register(tool: AgentTool)
    fun unregister(toolId: String)
    fun get(toolId: String): AgentTool?
    fun getAll(): List<AgentTool>
    fun findByCapability(capability: String): List<AgentTool>
    fun getDescriptions(): List<ToolDescription>
}
