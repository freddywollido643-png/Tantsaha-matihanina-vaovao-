package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFF81C784),
  onPrimary = Color(0xFF003300),
  primaryContainer = DarkGreenPrimary,
  onPrimaryContainer = Color(0xFFC8E6C9),
  secondary = GoldSecondaryBright,
  onSecondary = Color(0xFF3E2723),
  secondaryContainer = Color(0xFF5D4037),
  onSecondaryContainer = GoldSecondaryContainer,
  tertiary = Color(0xFFA1887F),
  background = DarkBackground,
  surface = DarkSurface,
  surfaceVariant = DarkSurfaceVariant,
  onBackground = Color(0xFFE8ECE3),
  onSurface = Color(0xFFE8ECE3)
)

private val LightColorScheme = lightColorScheme(
  primary = DarkGreenPrimary,
  onPrimary = OnDarkGreenPrimary,
  primaryContainer = DarkGreenPrimaryContainer,
  onPrimaryContainer = Color(0xFF002105),
  secondary = GoldSecondary,
  onSecondary = OnGoldSecondary,
  secondaryContainer = GoldSecondaryContainer,
  onSecondaryContainer = Color(0xFF281800),
  tertiary = EarthTertiary,
  tertiaryContainer = EarthTertiaryContainer,
  background = LightBackground,
  surface = LightSurface,
  surfaceVariant = LightSurfaceVariant,
  onBackground = Color(0xFF191C19),
  onSurface = Color(0xFF191C19)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set default false so brand green/gold is prominent
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

