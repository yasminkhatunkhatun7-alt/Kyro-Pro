package com.kyro.app.core.agent.state

enum class TaskStatus {
    QUEUED,
    PLANNING,
    PLAN_READY,
    RUNNING,
    WAITING_APPROVAL,
    VERIFYING,
    RECOVERING,
    COMPLETED,
    FAILED,
    CANCELLED
}
