package com.kyro.app.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GeminiAIProvider(private val apiKey: String) : AIProvider {
    private val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"

    override suspend fun isAvailable(): Boolean = apiKey.isNotBlank()

    override suspend fun generateResponse(prompt: String): AIResponse = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext AIResponse.Error("API key not configured")
        try {
            val url = URL("$endpoint?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = """
                {
                  "contents": [{
                    "parts": [{"text": "${prompt.replace("\"", "\\\"").replace("\n", "\\n")} "}]
                  }]
                }
            """.trimIndent()

            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(responseString)
                val candidates = jsonObject.getJSONArray("candidates")
                val text = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
                AIResponse.Success(text)
            } else {
                val errorString = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                AIResponse.Error("Gemini API error: ${connection.responseCode} - $errorString")
            }
        } catch (e: Exception) {
            AIResponse.Error(e.message ?: "Failed to connect to Gemini")
        }
    }
}
