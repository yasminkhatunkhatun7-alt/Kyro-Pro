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
import com.kyro.app.ui.screens.ChatScreen
import com.kyro.app.ui.screens.SetupScreen
import com.kyro.app.ui.theme.DeepObsidian
import com.kyro.app.voice.VoiceManager

@Composable
fun KyroApp(voiceManager: VoiceManager, viewModel: KyroViewModel, modifier: Modifier = Modifier) {
    var currentDest by remember { mutableStateOf(NavDest.HOME) }

    val isListening by voiceManager.isListening.collectAsState()
    val spokenText by voiceManager.spokenText.collectAsState()

    val messages by viewModel.messages.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    val orbState = when {
        isListening -> OrbState.LISTENING
        isProcessing -> OrbState.THINKING
        else -> OrbState.IDLE
    }

    // Automatically submit voice transcription when recognized
    LaunchedEffect(spokenText) {
        if (spokenText.isNotBlank()) {
            viewModel.submitRequest(spokenText)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
    ) {
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
                    ChatScreen(
                        messages = messages,
                        isThinking = orbState == OrbState.THINKING,
                        onSendMessage = { text -> viewModel.submitRequest(text) }
                    )
                }
                NavDest.SETUP -> {
                    SetupScreen()
                }
            }
        }

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
