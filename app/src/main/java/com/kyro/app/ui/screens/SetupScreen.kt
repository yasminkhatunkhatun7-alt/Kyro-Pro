package com.kyro.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyro.app.ui.components.glassmorphism
import com.kyro.app.ui.theme.Amber
import com.kyro.app.ui.theme.DarkGraphite
import com.kyro.app.ui.theme.Pewter

@Composable
fun SetupScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 60.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "SYSTEM CONFIGURATION",
                color = Amber,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            SettingCard(title = "Local AI Engine", description = "Prefer on-device processing", initialValue = true)
        }
        item {
            SettingCard(title = "Proactive Insights", description = "Allow KYRO to anticipate needs", initialValue = false)
        }
        item {
            SettingCard(title = "Deep Memory", description = "Store context across sessions", initialValue = true)
        }
        item {
            SettingCard(title = "Haptic Feedback", description = "Physical response on interactions", initialValue = true)
        }
    }
}

@Composable
fun SettingCard(title: String, description: String, initialValue: Boolean) {
    var isEnabled by remember { mutableStateOf(initialValue) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(cornerRadius = 24.dp)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, color = Pewter, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            FuturisticToggle(checked = isEnabled, onCheckedChange = { isEnabled = it })
        }
    }
}

@Composable
fun FuturisticToggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val trackWidth = 48.dp
    val trackHeight = 24.dp
    val thumbSize = 20.dp

    val bias by animateFloatAsState(
        targetValue = if (checked) 1f else -1f,
        animationSpec = tween(300),
        label = "toggle_bias"
    )
    val trackColor = if (checked) Amber.copy(alpha = 0.5f) else DarkGraphite
    val thumbColor = if (checked) Amber else Pewter

    Box(
        modifier = Modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(CircleShape)
            .background(trackColor)
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(thumbColor)
                    .align(
                        if (checked) Alignment.CenterEnd else Alignment.CenterStart
                    )
            )
        }
    }
}
