package com.kyro.app.core.agent.planner

class DependencyResolver {
    fun resolveExecutionOrder(steps: List<PlanStep>): List<PlanStep> {
        // Simplified: return steps sorted by order, assuming PlanValidator checks DAG validity.
        return steps.sortedBy { it.order }
    }
}
