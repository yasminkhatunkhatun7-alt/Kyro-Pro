package com.kyro.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyro.app.ui.components.glassmorphism
import com.kyro.app.ui.theme.Amber
import com.kyro.app.ui.theme.DarkGraphite
import com.kyro.app.ui.theme.Pewter
import com.kyro.app.ui.theme.WarmIvory

data class ChatMessage(val id: String, val text: String, val isKyro: Boolean)

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    isThinking: Boolean,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                AnimatedVisibility(
                    visible = true,
                    enter = if (msg.isKyro) slideInHorizontally { -it } + fadeIn() else slideInHorizontally { it } + fadeIn()
                ) {
                    MessageBubble(msg)
                }
            }
            if (isThinking) {
                item {
                    ThinkingIndicator()
                }
            }
        }

        // Input Area
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 110.dp)
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.fillMaxWidth().glassmorphism(cornerRadius = 24.dp),
                placeholder = { Text("Ask KYRO...", color = Pewter) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = WarmIvory,
                    unfocusedTextColor = WarmIvory,
                    cursorColor = Amber,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    }
                ),
                maxLines = 3
            )
        }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage) {
    val alignment = if (msg.isKyro) Alignment.CenterStart else Alignment.CenterEnd
    val color = if (msg.isKyro) WarmIvory else Pewter
    val shadowAlpha = if (msg.isKyro) 0.3f else 0f

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Text(
            text = msg.text,
            color = color,
            fontSize = if (msg.isKyro) 18.sp else 16.sp,
            fontWeight = if (msg.isKyro) FontWeight.Light else FontWeight.Normal,
            modifier = Modifier
                .padding(8.dp)
                .shadow(elevation = if (msg.isKyro) 8.dp else 0.dp, ambientColor = Amber.copy(alpha = shadowAlpha))
        )
    }
}

@Composable
fun ThinkingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "thinking_alpha"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(Color.Transparent)
        ) {
            Text("O", color = Amber, modifier = Modifier.alpha(alpha))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("Thinking...", color = Amber.copy(alpha = alpha), fontSize = 14.sp)
    }
}
