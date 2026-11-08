package com.kyro.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kyro.app.ui.theme.Amber
import com.kyro.app.ui.theme.PaleGold

enum class NavDest {
    HOME, CHAT, SETUP
}

@Composable
fun KyroNavigationBar(
    currentDest: NavDest,
    onNavigate: (NavDest) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .fillMaxWidth()
            .height(72.dp)
            .glassmorphism(cornerRadius = 36.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                isSelected = currentDest == NavDest.HOME,
                onClick = { onNavigate(NavDest.HOME) }
            )
            NavItem(
                icon = Icons.Default.List,
                isSelected = currentDest == NavDest.CHAT,
                onClick = { onNavigate(NavDest.CHAT) }
            )
            NavItem(
                icon = Icons.Default.Settings,
                isSelected = currentDest == NavDest.SETUP,
                onClick = { onNavigate(NavDest.SETUP) }
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.4f,
        animationSpec = tween(300),
        label = "nav_alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = tween(300),
        label = "nav_scale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Soft amber glow behind the active icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Amber.copy(alpha = 0.3f), CircleShape)
                    .blur(8.dp)
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PaleGold.copy(alpha = alpha),
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        )
    }
}
