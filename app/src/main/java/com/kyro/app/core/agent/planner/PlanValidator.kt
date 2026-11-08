package com.kyro.app.core.agent.planner

import com.kyro.app.core.agent.tools.ToolRegistry

class PlanValidator(private val toolRegistry: ToolRegistry) {
    fun validate(plan: AgentPlan): Boolean {
        if (plan.steps.isEmpty()) return false
        val stepIds = mutableSetOf<String>()
        for (step in plan.steps) {
            if (!stepIds.add(step.id)) return false // duplicate step ID
            if (toolRegistry.get(step.toolId) == null) return false // unknown tool
            for (dep in step.dependsOn) {
                if (!stepIds.contains(dep)) return false // Invalid dependency order or non-existent
            }
        }
        return true
    }
}
