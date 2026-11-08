package com.kyro.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val KyroColorScheme = darkColorScheme(
    primary = Amber,
    secondary = PaleGold,
    tertiary = Pewter,
    background = DeepObsidian,
    surface = DarkGraphite,
    onPrimary = DeepObsidian,
    onSecondary = DeepObsidian,
    onTertiary = DeepObsidian,
    onBackground = WarmIvory,
    onSurface = WarmIvory
)

@Composable
fun KyroTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = KyroColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DeepObsidian.toArgb()
            window.navigationBarColor = DeepObsidian.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
