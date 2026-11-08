package com.kyro.app.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kyro.app.ui.components.KyroNavigationBar
import com.kyro.app.ui.components.KyroOrb
import com.kyro.app.ui.components.NavDest
import com.kyro.app.ui.components.OrbState
import com.kyro.app.ui.screens.ChatMessage
import com.kyro.app.ui.screens.ChatScreen
import com.kyro.app.ui.screens.SetupScreen
import com.kyro.app.ui.theme.DeepObsidian
import com.kyro.app.voice.VoiceManager

@Composable
fun KyroApp(voiceManager: VoiceManager, modifier: Modifier = Modifier) {
    var currentDest by remember { mutableStateOf(NavDest.HOME) }
    val isListening by voiceManager.isListening.collectAsState()
    val spokenText by voiceManager.spokenText.collectAsState()
    val assistantText by voiceManager.assistantText.collectAsState()

    // Derived state for the Orb
    val orbState = when {
        isListening -> OrbState.LISTENING
        assistantText.isNotEmpty() -> OrbState.THINKING
        else -> OrbState.IDLE
    }

    // Fake chat history for the new UI demonstration, hooking into VoiceManager
    val messages = remember { mutableStateListOf<ChatMessage>() }

    LaunchedEffect(spokenText) {
        if (spokenText.isNotEmpty() && (messages.isEmpty() || messages.last().text != spokenText)) {
            messages.add(ChatMessage(java.util.UUID.randomUUID().toString(), spokenText, false))
        }
    }
    LaunchedEffect(assistantText) {
         if (assistantText.isNotEmpty() && (messages.isEmpty() || messages.last().text != assistantText)) {
             messages.add(ChatMessage(java.util.UUID.randomUUID().toString(), assistantText, true))
         }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
    ) {
        // Main Content Area
        Crossfade(
            targetState = currentDest,
            animationSpec = tween(500),
            modifier = Modifier.fillMaxSize(),
            label = "main_content_crossfade"
        ) { dest ->
            when (dest) {
                NavDest.HOME -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        KyroOrb(state = orbState)
                        // Simple overlay to trigger listening in Home view
                        Button(
                            onClick = {
                                if (isListening) voiceManager.stopListening() else voiceManager.startListening()
                            },
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 120.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                             Text(if (isListening) "STOP" else "WAKE", color = Color.White.copy(alpha=0.5f))
                        }
                    }
                }
                NavDest.CHAT -> {
                    ChatScreen(messages = messages, isThinking = orbState == OrbState.THINKING)
                }
                NavDest.SETUP -> {
                    SetupScreen()
                }
            }
        }

        // Floating Bottom Navigation
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            KyroNavigationBar(
                currentDest = currentDest,
                onNavigate = { currentDest = it }
            )
        }
    }
}
