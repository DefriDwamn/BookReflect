package com.defri.bookreflect.presentation.common.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

private val Lavender = Color(0xFFE6E6FA)
private val SoftPurple = Color(0xFFB19CD9)
private val DeepPurple = Color(0xFF7B1FA2)
private val DarkPurple = Color(0xFF4A148C)
private val Plum = Color(0xFF8E44AD)
private val Orchid = Color(0xFFDA70D6)
private val Mauve = Color(0xFFE0B0FF)
private val Lilac = Color(0xFFC8A2C8)

// Text colors
private val TextDark = Color(0xFF2C2C2C)
private val TextLight = Color(0xFFF5F5F5)
private val TextSecondaryDark = Color(0xFF666666)
private val TextSecondaryLight = Color(0xFFB3B3B3)

// Dark theme colors
private val DarkBackground = Color(0xFF1A1A2E)
private val DarkSurface = Color(0xFF2D2D44)
private val DarkSurfaceVariant = Color(0xFF3D3D54)
private val DarkError = Color(0xFFCF6679)

private val DarkColorScheme = darkColorScheme(
    primary = SoftPurple,
    onPrimary = TextLight,
    primaryContainer = Plum,
    onPrimaryContainer = TextLight,
    
    secondary = Orchid,
    onSecondary = TextLight,
    secondaryContainer = Plum,
    onSecondaryContainer = TextLight,
    
    tertiary = Mauve,
    onTertiary = TextLight,
    tertiaryContainer = Plum,
    onTertiaryContainer = TextLight,
    
    background = DarkBackground,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    
    error = DarkError,
    onError = TextLight,
    errorContainer = DarkError,
    onErrorContainer = TextLight,
    
    outline = SoftPurple,
    outlineVariant = SoftPurple.copy(alpha = 0.5f),
    
    scrim = DarkPurple.copy(alpha = 0.32f),
    inverseSurface = TextLight,
    inverseOnSurface = DarkPurple,
    inversePrimary = Plum
)

private val LightColorScheme = lightColorScheme(
    primary = DeepPurple,
    onPrimary = TextLight,
    primaryContainer = SoftPurple,
    onPrimaryContainer = TextDark,
    
    secondary = Plum,
    onSecondary = TextLight,
    secondaryContainer = Lilac,
    onSecondaryContainer = TextDark,
    
    tertiary = Orchid,
    onTertiary = TextLight,
    tertiaryContainer = Mauve,
    onTertiaryContainer = TextDark,
    
    background = Color(0xFFF8F8FF),
    onBackground = TextDark,
    surface = Color(0xFFFFFFFF),
    onSurface = TextDark,
    surfaceVariant = SoftPurple.copy(alpha = 0.1f),
    onSurfaceVariant = TextSecondaryDark,
    
    error = DarkError,
    onError = TextLight,
    errorContainer = DarkError.copy(alpha = 0.1f),
    onErrorContainer = TextDark,
    
    outline = DeepPurple,
    outlineVariant = DeepPurple.copy(alpha = 0.5f),
    
    scrim = DarkPurple.copy(alpha = 0.32f),
    inverseSurface = TextDark,
    inverseOnSurface = TextLight,
    inversePrimary = SoftPurple
)

@Composable
fun BookReflectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    SideEffect {
        val window = (context as? Activity)?.window
        window?.statusBarColor = colorScheme.primary.toArgb()
        val insetsController = window?.let { WindowInsetsControllerCompat(it, view) }
        insetsController?.isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
