package com.kyro.app.ai

interface AIProvider {
    suspend fun generateResponse(prompt: String): AIResponse
    suspend fun isAvailable(): Boolean
}

sealed class AIResponse {
    data class Success(val text: String) : AIResponse()
    data class Error(val message: String) : AIResponse()
}
