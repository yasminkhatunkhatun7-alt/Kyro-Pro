package com.kyro.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.kyro.app.action.ActionExecutor
import com.kyro.app.voice.VoiceManager

class MainActivity : ComponentActivity() {

    private lateinit var voiceManager: VoiceManager
    private lateinit var actionExecutor: ActionExecutor

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
            if (audioGranted) {
                // Permission granted
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        voiceManager = VoiceManager(this)
        actionExecutor = ActionExecutor(this)

        voiceManager.onResult = { result ->
            processCommand(result)
        }

        checkPermissions()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KyroApp(voiceManager = voiceManager)
                }
            }
        }
    }

    private fun processCommand(command: String) {
        val lowerCommand = command.lowercase()
        val response = when {
            lowerCommand.contains("open ") || lowerCommand.contains(" kholo") -> {
                val appName = lowerCommand.replace("open ", "").replace(" kholo", "").trim()
                actionExecutor.openApp(appName)
            }
            lowerCommand.contains("search youtube for ") -> {
                val query = lowerCommand.replace("search youtube for ", "").trim()
                actionExecutor.searchYouTube(query)
            }
            else -> "I heard: \$command"
        }

        voiceManager.speak(response)
    }

    private fun checkPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS
        )

        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (missingPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(missingPermissions)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceManager.release()
    }
}
