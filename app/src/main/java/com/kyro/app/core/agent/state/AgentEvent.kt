package com.kyro.app.core.agent.state

import com.kyro.app.core.agent.planner.AgentPlan

sealed class AgentEvent {
    abstract val taskId: String
    abstract val timestamp: Long

    data class TaskCreated(override val taskId: String, val goal: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class PlanReady(override val taskId: String, val plan: AgentPlan, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class StepStarted(override val taskId: String, val stepId: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class StepCompleted(override val taskId: String, val stepId: String, val resultMessage: String?, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class StepFailed(override val taskId: String, val stepId: String, val error: String, val recoverable: Boolean, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class ProgressUpdated(override val taskId: String, val completedSteps: Int, val totalSteps: Int, val message: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class ApprovalRequired(override val taskId: String, val stepId: String, val reason: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class ApprovalGranted(override val taskId: String, val stepId: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class ApprovalRejected(override val taskId: String, val stepId: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class VerificationStarted(override val taskId: String, val stepId: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class VerificationCompleted(override val taskId: String, val stepId: String, val success: Boolean, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class RetryStarted(override val taskId: String, val stepId: String, val attemptCount: Int, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class RecoveryStarted(override val taskId: String, val reason: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class TaskPaused(override val taskId: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class TaskResumed(override val taskId: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class TaskCancelled(override val taskId: String, val reason: String? = null, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class TaskCompleted(override val taskId: String, val message: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
    data class TaskFailed(override val taskId: String, val error: String, override val timestamp: Long = System.currentTimeMillis()) : AgentEvent()
}
