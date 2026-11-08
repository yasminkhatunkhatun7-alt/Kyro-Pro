package com.kyro.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyro.app.ui.theme.TranslucentGlass

fun Modifier.glassmorphism(
    cornerRadius: Dp = 16.dp,
    borderAlpha: Float = 0.2f
): Modifier = this
    .shadow(
        elevation = 20.dp,
        shape = RoundedCornerShape(cornerRadius),
        spotColor = Color.Black.copy(alpha = 0.5f),
        ambientColor = Color.Black.copy(alpha = 0.3f)
    )
    .clip(RoundedCornerShape(cornerRadius))
    .background(
        brush = Brush.linearGradient(
            colors = listOf(
                TranslucentGlass,
                TranslucentGlass.copy(alpha = 0.1f)
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = borderAlpha),
                Color.Transparent,
                Color.White.copy(alpha = borderAlpha * 0.5f)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )
