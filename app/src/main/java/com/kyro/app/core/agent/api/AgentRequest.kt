package com.kyro.app.core.agent.api

data class AgentRequest(
    val id: String,
    val userGoal: String,
    val conversationId: String,
    val userId: String? = null,
    val source: RequestSource = RequestSource.CHAT,
    val mode: AgentMode = AgentMode.AUTO,
    val context: AgentRequestContext = AgentRequestContext(),
    val config: AgentConfig = AgentConfig()
)

enum class RequestSource {
    CHAT,
    LIVE_VOICE,
    QUICK_ACTION,
    SCHEDULED_TASK
}

enum class AgentMode {
    AUTO,
    FAST,
    REASONING,
    AGENT,
    OFFLINE
}

data class AgentRequestContext(
    val previousMessages: List<String> = emptyList(),
    val relevantMemoryIds: List<String> = emptyList(),
    val appContext: String? = null,
    val locale: String = "hi-IN"
)
