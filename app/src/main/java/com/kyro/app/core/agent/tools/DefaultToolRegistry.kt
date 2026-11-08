package com.kyro.app.core.agent.tools

class DefaultToolRegistry : ToolRegistry {
    private val tools = mutableMapOf<String, AgentTool>()

    override fun register(tool: AgentTool) {
        if (tools.containsKey(tool.id)) {
            throw IllegalArgumentException("Tool with ID \${tool.id} is already registered.")
        }
        tools[tool.id] = tool
    }

    override fun unregister(toolId: String) {
        tools.remove(toolId)
    }

    override fun get(toolId: String): AgentTool? = tools[toolId]

    override fun getAll(): List<AgentTool> = tools.values.toList()

    override fun findByCapability(capability: String): List<AgentTool> {
        return tools.values.filter { it.description.contains(capability, ignoreCase = true) }
    }

    override fun getDescriptions(): List<ToolDescription> {
        return tools.values.map {
            ToolDescription(it.id, it.name, it.description, it.inputSchema)
        }
    }
}
