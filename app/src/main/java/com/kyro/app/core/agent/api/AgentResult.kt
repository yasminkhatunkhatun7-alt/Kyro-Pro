package com.kyro.app.core.agent.api

import kotlinx.serialization.json.JsonObject

sealed class AgentResult {
    abstract val taskId: String
    abstract val message: String
    abstract val completedSteps: Int
    abstract val totalSteps: Int

    data class Success(
        override val taskId: String,
        override val message: String,
        override val completedSteps: Int,
        override val totalSteps: Int,
        val artifacts: JsonObject? = null
    ) : AgentResult()

    data class Failure(
        override val taskId: String,
        override val message: String,
        override val completedSteps: Int,
        override val totalSteps: Int,
        val recoverable: Boolean,
        val errorDetails: String? = null
    ) : AgentResult()

    data class Cancelled(
        override val taskId: String,
        override val message: String,
        override val completedSteps: Int,
        override val totalSteps: Int
    ) : AgentResult()
}
