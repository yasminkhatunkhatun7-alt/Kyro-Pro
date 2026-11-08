package com.kyro.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kyro.app.action.ActionExecutor
import com.kyro.app.ai.GeminiAIProvider
import com.kyro.app.core.agent.api.*
import com.kyro.app.core.agent.execution.DefaultAgentExecutor
import com.kyro.app.core.agent.planner.*
import com.kyro.app.core.agent.policy.*
import com.kyro.app.core.agent.state.*
import com.kyro.app.core.agent.tools.DefaultToolRegistry
import com.kyro.app.core.agent.tools.impl.AndroidFlashlightTool
import com.kyro.app.core.agent.tools.impl.AndroidOpenAppTool
import com.kyro.app.security.KeystoreManager
import com.kyro.app.ui.screens.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KyroViewModel(application: Application) : AndroidViewModel(application) {

    private val keystoreManager = KeystoreManager(application)
    private val toolRegistry = DefaultToolRegistry()
    private val policyEngine = DefaultPolicyEngine()
    private val approvalManager = DefaultApprovalManager()
    private val taskStateManager = DefaultTaskStateManager()
    private val actionExecutor = ActionExecutor(application)

    // Load API Key securely via EncryptedSharedPreferences
    private val aiProvider = GeminiAIProvider(keystoreManager.getApiKey() ?: "")

    private val taskPlanner = DefaultTaskPlanner(
        aiProvider,
        toolRegistry,
        PlanValidator(toolRegistry),
        DependencyResolver()
    )

    private val agentExecutor = DefaultAgentExecutor(
        toolRegistry,
        policyEngine,
        approvalManager
    )

    private val agentEngine = DefaultAgentEngine(
        taskPlanner,
        agentExecutor,
        taskStateManager,
        viewModelScope
    )

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    init {
        toolRegistry.register(AndroidOpenAppTool(actionExecutor))
        toolRegistry.register(AndroidFlashlightTool(actionExecutor))

        viewModelScope.launch {
            taskStateManager.events.collect { event ->
                when (event) {
                    is AgentEvent.TaskCreated -> {
                        _isProcessing.value = true
                        addMessage(event.goal, false)
                    }
                    is AgentEvent.TaskCompleted -> {
                        _isProcessing.value = false
                        addMessage(event.message, true)
                    }
                    is AgentEvent.TaskFailed -> {
                        _isProcessing.value = false
                        addMessage("Task Failed: ${event.error}", true)
                    }
                    is AgentEvent.TaskCancelled -> {
                        _isProcessing.value = false
                        addMessage("Task Cancelled.", true)
                    }
                    else -> {}
                }
            }
        }
    }

    fun submitRequest(goal: String) {
        if (goal.isBlank()) return

        if (keystoreManager.getApiKey().isNullOrBlank()) {
             addMessage("API Key is missing. Please configure it in Settings.", true)
             return
        }

        val request = AgentRequest(
            id = java.util.UUID.randomUUID().toString(),
            userGoal = goal,
            conversationId = "default_session"
        )

        viewModelScope.launch {
            agentEngine.execute(request)
        }
    }

    fun saveApiKey(key: String) {
        keystoreManager.saveApiKey(key)
    }

    private fun addMessage(text: String, isKyro: Boolean) {
        val newMsg = ChatMessage(java.util.UUID.randomUUID().toString(), text, isKyro)
        _messages.value = _messages.value + newMsg
    }
}
