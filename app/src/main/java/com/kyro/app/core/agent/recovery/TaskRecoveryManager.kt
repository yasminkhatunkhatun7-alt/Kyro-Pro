package com.kyro.app.core.agent.recovery

import com.kyro.app.core.agent.api.AgentConfig
import com.kyro.app.core.agent.planner.PlanStep

data class RetryPolicy(
    val maxRetries: Int,
    val backoffMillis: Long = 1000L
)

interface TaskRecoveryManager {
    suspend fun canRecover(step: PlanStep, attemptCount: Int, config: AgentConfig): Boolean
    suspend fun delayForRecovery(attemptCount: Int)
}

class DefaultTaskRecoveryManager : TaskRecoveryManager {
    override suspend fun canRecover(step: PlanStep, attemptCount: Int, config: AgentConfig): Boolean {
        return attemptCount < config.maxRetriesPerStep
    }

    override suspend fun delayForRecovery(attemptCount: Int) {
        kotlinx.coroutines.delay(1000L * attemptCount)
    }
}
