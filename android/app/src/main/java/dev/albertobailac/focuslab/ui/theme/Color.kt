package dev.albertobailac.focuslab.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val CanvasLight = Color(0xFFF7F8FC)
private val CanvasDark = Color(0xFF101118)
private val SurfacePrimaryLight = Color(0xFFFFFFFF)
private val SurfacePrimaryDark = Color(0xFF191B24)
private val SurfaceSecondaryLight = Color(0xFFEEF0F7)
private val SurfaceSecondaryDark = Color(0xFF242734)
private val ContentPrimaryLight = Color(0xFF181B25)
private val ContentPrimaryDark = Color(0xFFF2F3FA)
private val ContentSecondaryLight = Color(0xFF565E70)
private val ContentSecondaryDark = Color(0xFFC5C8D4)
private val ActionPrimaryLight = Color(0xFF4F46E5)
private val ActionPrimaryDark = Color(0xFFC5C4FF)
private val ActionOnPrimaryLight = Color(0xFFFFFFFF)
private val ActionOnPrimaryDark = Color(0xFF211A69)
private val ActionContainerLight = Color(0xFFE5E7FF)
private val ActionContainerDark = Color(0xFF373281)
private val ActionOnContainerLight = Color(0xFF272160)
private val ActionOnContainerDark = Color(0xFFE4E3FF)
private val StatusPausedLight = Color(0xFF9A4D00)
private val StatusPausedDark = Color(0xFFFFB775)
private val StatusCompletedLight = Color(0xFF176B4D)
private val StatusCompletedDark = Color(0xFF73D5A9)
private val BorderSubtleLight = Color(0xFF747C8E)
private val BorderSubtleDark = Color(0xFF8E92A1)
private val ErrorLight = Color(0xFFB3261E)
private val ErrorDark = Color(0xFFFFB4AB)

internal val FocusLabLightColorScheme = lightColorScheme(
    primary = ActionPrimaryLight,
    onPrimary = ActionOnPrimaryLight,
    primaryContainer = ActionContainerLight,
    onPrimaryContainer = ActionOnContainerLight,
    secondary = StatusCompletedLight,
    onSecondary = SurfacePrimaryLight,
    tertiary = StatusPausedLight,
    onTertiary = SurfacePrimaryLight,
    background = CanvasLight,
    onBackground = ContentPrimaryLight,
    surface = SurfacePrimaryLight,
    onSurface = ContentPrimaryLight,
    surfaceVariant = SurfaceSecondaryLight,
    onSurfaceVariant = ContentSecondaryLight,
    outline = BorderSubtleLight,
    error = ErrorLight,
)

internal val FocusLabDarkColorScheme = darkColorScheme(
    primary = ActionPrimaryDark,
    onPrimary = ActionOnPrimaryDark,
    primaryContainer = ActionContainerDark,
    onPrimaryContainer = ActionOnContainerDark,
    secondary = StatusCompletedDark,
    onSecondary = CanvasDark,
    tertiary = StatusPausedDark,
    onTertiary = CanvasDark,
    background = CanvasDark,
    onBackground = ContentPrimaryDark,
    surface = SurfacePrimaryDark,
    onSurface = ContentPrimaryDark,
    surfaceVariant = SurfaceSecondaryDark,
    onSurfaceVariant = ContentSecondaryDark,
    outline = BorderSubtleDark,
    error = ErrorDark,
)
