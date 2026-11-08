package com.kyro.app.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GroqAIProvider(private val apiKey: String) : AIProvider {
    private val endpoint = "https://api.groq.com/openai/v1/chat/completions"

    override suspend fun isAvailable(): Boolean = apiKey.isNotBlank()

    override suspend fun generateResponse(prompt: String): AIResponse = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext AIResponse.Error("API key not configured")
        try {
            val url = URL(endpoint)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = """
                {
                  "model": "llama3-8b-8192",
                  "messages": [{"role": "user", "content": "${prompt.replace("\"", "\\\"").replace("\n", "\\n")}"}]
                }
            """.trimIndent()

            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(responseString)
                val choices = jsonObject.getJSONArray("choices")
                val text = choices.getJSONObject(0).getJSONObject("message").getString("content")
                AIResponse.Success(text)
            } else {
                val errorString = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                AIResponse.Error("Groq API error: ${connection.responseCode} - $errorString")
            }
        } catch (e: Exception) {
            AIResponse.Error(e.message ?: "Failed to connect to Groq")
        }
    }
}
