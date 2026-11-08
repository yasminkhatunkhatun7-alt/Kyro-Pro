package com.kyro.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kyro.app.voice.VoiceManager

@Composable
fun KyroApp(voiceManager: VoiceManager, modifier: Modifier = Modifier) {
    val isListening by voiceManager.isListening.collectAsState()
    val spokenText by voiceManager.spokenText.collectAsState()
    val assistantText by voiceManager.assistantText.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(1f))

        // Response
        if (assistantText.isNotEmpty()) {
            Text(
                text = assistantText,
                color = Color(0xFF3DDC84),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Floating Orb representation
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = if (isListening) Color(0xFF3DDC84) else Color.DarkGray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "KYRO",
                color = if (isListening) Color.Black else Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recognized text
        Text(
            text = spokenText.ifEmpty { "Say something..." },
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { voiceManager.startListening() }) {
                Text("Listen")
            }
            Button(onClick = { voiceManager.stopListening() }) {
                Text("Stop")
            }
        }
    }
}
