package com.kyro.app.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CloudAIProvider : AIProvider {

    // In a real implementation, API keys should be retrieved from a secure SharedPreferences/Keystore,
    // NOT hardcoded here.
    private var isEnabled = true

    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        // Implement real network check or API key presence check
        isEnabled
    }

    override suspend fun generateResponse(prompt: String): AIResponse = withContext(Dispatchers.IO) {
        try {
            // Skeleton for Gemini or Groq network call
            // e.g. using Retrofit or HttpURLConnection

            // Simulating network delay
            Thread.sleep(1000)

            AIResponse.Success("This is a simulated cloud response for: \$prompt")
        } catch (e: Exception) {
            AIResponse.Error("Cloud AI failed: \${e.message}")
        }
    }
}
