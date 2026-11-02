package com.moneyflow.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Blue700,
    onPrimary = CardBackground,
    primaryContainer = Blue50,
    onPrimaryContainer = Blue700,
    secondary = Green700,
    onSecondary = CardBackground,
    secondaryContainer = Green100,
    onSecondaryContainer = Green700,
    error = Red700,
    onError = CardBackground,
    errorContainer = Red100,
    onErrorContainer = Red700,
    background = SurfaceLight,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor
)

@Composable
fun MoneyFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
