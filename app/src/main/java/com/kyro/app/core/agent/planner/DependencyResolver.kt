package com.kyro.app.core.agent.planner

import java.util.LinkedList
import java.util.Queue

class DependencyResolver {
    /**
     * Performs a topological sort of the PlanSteps based on their dependencies.
     */
    fun resolveExecutionOrder(steps: List<PlanStep>): List<PlanStep> {
        val stepMap = steps.associateBy { it.id }
        val inDegree = mutableMapOf<String, Int>()
        val adjList = mutableMapOf<String, MutableList<String>>()

        // Initialize structures
        for (step in steps) {
            inDegree[step.id] = 0
            adjList[step.id] = mutableListOf()
        }

        // Build graph
        for (step in steps) {
            for (depId in step.dependsOn) {
                // depId must execute BEFORE step.id
                adjList[depId]?.add(step.id)
                inDegree[step.id] = (inDegree[step.id] ?: 0) + 1
            }
        }

        val queue: Queue<String> = LinkedList()
        for ((node, degree) in inDegree) {
            if (degree == 0) {
                queue.add(node)
            }
        }

        val sortedOrder = mutableListOf<PlanStep>()

        while (queue.isNotEmpty()) {
            val curr = queue.poll()!!
            stepMap[curr]?.let { sortedOrder.add(it) }

            for (neighbor in adjList[curr] ?: emptyList()) {
                inDegree[neighbor] = (inDegree[neighbor] ?: 1) - 1
                if (inDegree[neighbor] == 0) {
                    queue.add(neighbor)
                }
            }
        }

        // If sortedOrder doesn't contain all steps, there's a cycle, but PlanValidator should catch it first.
        return if (sortedOrder.size == steps.size) sortedOrder else steps
    }
}
