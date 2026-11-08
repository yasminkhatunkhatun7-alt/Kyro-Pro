package com.kyro.app.core.agent.planner

import com.kyro.app.core.agent.tools.ToolRegistry

class PlanValidator(private val toolRegistry: ToolRegistry) {
    fun validate(plan: AgentPlan): Boolean {
        if (plan.steps.isEmpty()) return false
        val stepIds = mutableSetOf<String>()

        for (step in plan.steps) {
            if (!stepIds.add(step.id)) return false // duplicate step ID
            if (toolRegistry.get(step.toolId) == null) return false // unknown tool
            if (step.dependsOn.contains(step.id)) return false // self-dependency
        }

        // Check for missing dependencies
        for (step in plan.steps) {
            for (dep in step.dependsOn) {
                if (!stepIds.contains(dep)) return false
            }
        }

        if (hasCycle(plan.steps)) return false

        return true
    }

    private fun hasCycle(steps: List<PlanStep>): Boolean {
        val adjList = mutableMapOf<String, List<String>>()
        steps.forEach { adjList[it.id] = it.dependsOn }

        val visited = mutableSetOf<String>()
        val recursionStack = mutableSetOf<String>()

        fun dfs(node: String): Boolean {
            if (recursionStack.contains(node)) return true
            if (visited.contains(node)) return false

            visited.add(node)
            recursionStack.add(node)

            for (neighbor in adjList[node] ?: emptyList()) {
                if (dfs(neighbor)) return true
            }

            recursionStack.remove(node)
            return false
        }

        for (step in steps) {
            if (dfs(step.id)) return true
        }

        return false
    }
}
