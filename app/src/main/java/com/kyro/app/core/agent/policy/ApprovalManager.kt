package com.kyro.app.core.agent.policy

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ApprovalState {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED,
    CANCELLED
}

data class ApprovalRequest(
    val id: String,
    val taskId: String,
    val toolId: String,
    val description: String,
    val state: ApprovalState = ApprovalState.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

interface ApprovalManager {
    val pendingRequests: StateFlow<List<ApprovalRequest>>
    suspend fun requestApproval(taskId: String, toolId: String, description: String): String
    suspend fun approve(requestId: String)
    suspend fun reject(requestId: String)
}

class DefaultApprovalManager : ApprovalManager {
    private val _requests = MutableStateFlow<List<ApprovalRequest>>(emptyList())
    override val pendingRequests: StateFlow<List<ApprovalRequest>> = _requests.asStateFlow()

    override suspend fun requestApproval(taskId: String, toolId: String, description: String): String {
        val request = ApprovalRequest(
            id = java.util.UUID.randomUUID().toString(),
            taskId = taskId,
            toolId = toolId,
            description = description
        )
        _requests.value = _requests.value + request
        return request.id
    }

    override suspend fun approve(requestId: String) {
        updateState(requestId, ApprovalState.APPROVED)
    }

    override suspend fun reject(requestId: String) {
        updateState(requestId, ApprovalState.REJECTED)
    }

    private fun updateState(requestId: String, state: ApprovalState) {
        _requests.value = _requests.value.map {
            if (it.id == requestId) it.copy(state = state) else it
        }
    }
}
