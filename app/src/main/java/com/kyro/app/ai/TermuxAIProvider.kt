package com.kyro.app.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class TermuxAIProvider(private val endpointUrl: String = "http://localhost:8080/v1/chat/completions") : AIProvider {

    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL(endpointUrl.replace("/chat/completions", "/models"))
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 2000
            connection.readTimeout = 2000
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode
            connection.disconnect()
            responseCode == 200
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun generateResponse(prompt: String): AIResponse = withContext(Dispatchers.IO) {
        try {
            val url = URL(endpointUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = """
                {
                  "messages": [{"role": "user", "content": "$prompt"}],
                  "temperature": 0.7
                }
            """.trimIndent()

            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                // Basic parsing, assuming standard OpenAI compatible format
                val jsonObject = JSONObject(responseString)
                val choices = jsonObject.getJSONArray("choices")
                val text = choices.getJSONObject(0).getJSONObject("message").getString("content")
                AIResponse.Success(text)
            } else {
                AIResponse.Error("Local AI responded with code: $responseCode")
            }
        } catch (e: Exception) {
            AIResponse.Error(e.message ?: "Failed to connect to Local AI")
        }
    }
}
