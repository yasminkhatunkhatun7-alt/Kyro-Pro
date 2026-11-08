package com.kyro.app.core.agent.api

import com.kyro.app.core.agent.execution.AgentExecutor
import com.kyro.app.core.agent.planner.TaskPlanner
import com.kyro.app.core.agent.state.AgentEvent
import com.kyro.app.core.agent.state.TaskStateManager
import com.kyro.app.core.agent.state.TaskStatus
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

class DefaultAgentEngine(
    private val taskPlanner: TaskPlanner,
    private val agentExecutor: AgentExecutor,
    private val stateManager: TaskStateManager,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) : AgentEngine {

    private val activeJobs = ConcurrentHashMap<String, Job>()

    override suspend fun execute(request: AgentRequest): AgentResult {
        stateManager.updateStatus(request.id, TaskStatus.QUEUED)
        stateManager.emitEvent(AgentEvent.TaskCreated(request.id, request.userGoal))

        val deferred = CompletableDeferred<AgentResult>()

        val job = coroutineScope.launch {
            try {
                stateManager.updateStatus(request.id, TaskStatus.PLANNING)
                val planResult = taskPlanner.plan(request)

                if (planResult == null) {
                    val failure = AgentResult.Failure(request.id, "Failed to generate plan.", 0, 0, false)
                    stateManager.updateStatus(request.id, TaskStatus.FAILED)
                    stateManager.emitEvent(AgentEvent.TaskFailed(request.id, "Planning failed."))
                    deferred.complete(failure)
                    return@launch
                }

                stateManager.updateStatus(request.id, TaskStatus.PLAN_READY)
                stateManager.emitEvent(AgentEvent.PlanReady(request.id, planResult))

                stateManager.updateStatus(request.id, TaskStatus.RUNNING)
                val result = agentExecutor.executePlan(planResult)

                when (result) {
                    is AgentResult.Success -> {
                        stateManager.updateStatus(request.id, TaskStatus.COMPLETED)
                        stateManager.emitEvent(AgentEvent.TaskCompleted(request.id, result.message))
                    }
                    is AgentResult.Failure -> {
                        stateManager.updateStatus(request.id, TaskStatus.FAILED)
                        stateManager.emitEvent(AgentEvent.TaskFailed(request.id, result.message))
                    }
                    is AgentResult.Cancelled -> {
                        stateManager.updateStatus(request.id, TaskStatus.CANCELLED)
                        stateManager.emitEvent(AgentEvent.TaskCancelled(request.id))
                    }
                }
                deferred.complete(result)
            } catch (e: CancellationException) {
                stateManager.updateStatus(request.id, TaskStatus.CANCELLED)
                stateManager.emitEvent(AgentEvent.TaskCancelled(request.id, e.message))
                deferred.complete(AgentResult.Cancelled(request.id, e.message ?: "Task cancelled", 0, 0))
            } catch (e: Exception) {
                stateManager.updateStatus(request.id, TaskStatus.FAILED)
                stateManager.emitEvent(AgentEvent.TaskFailed(request.id, e.message ?: "Unknown error"))
                deferred.complete(AgentResult.Failure(request.id, e.message ?: "Unknown error", 0, 0, false))
            } finally {
                activeJobs.remove(request.id)
            }
        }

        activeJobs[request.id] = job
        return deferred.await()
    }

    override fun cancel(taskId: String) {
        activeJobs[taskId]?.cancel()
    }
}
