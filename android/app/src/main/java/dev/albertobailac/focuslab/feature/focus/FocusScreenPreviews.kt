package dev.albertobailac.focuslab.feature.focus

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.albertobailac.focuslab.ui.theme.FocusLabTheme

@Preview(
    name = "Light",
    group = "FocusScreen",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 411,
    heightDp = 891,
)
@Preview(
    name = "Dark",
    group = "FocusScreen",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 411,
    heightDp = 891,
)
private annotation class LightDarkFocusScreenPreviews

@LightDarkFocusScreenPreviews
@Composable
private fun IdleFocusScreenPreview() {
    FocusScreenPreview(
        FocusUiState(
            durationMillis = DEFAULT_FOCUS_DURATION_MILLIS,
            remainingMillis = DEFAULT_FOCUS_DURATION_MILLIS,
            status = FocusTimerStatus.Idle,
        ),
    )
}

@LightDarkFocusScreenPreviews
@Composable
private fun RunningFocusScreenPreview() {
    FocusScreenPreview(
        FocusUiState(
            durationMillis = DEFAULT_FOCUS_DURATION_MILLIS,
            remainingMillis = 17L * 60L * 1_000L + 42L * 1_000L,
            status = FocusTimerStatus.Running,
        ),
    )
}

@LightDarkFocusScreenPreviews
@Composable
private fun PausedFocusScreenPreview() {
    FocusScreenPreview(
        FocusUiState(
            durationMillis = LONG_FOCUS_DURATION_MILLIS,
            remainingMillis = 31L * 60L * 1_000L + 15L * 1_000L,
            status = FocusTimerStatus.Paused,
        ),
    )
}

@LightDarkFocusScreenPreviews
@Composable
private fun CompletedFocusScreenPreview() {
    FocusScreenPreview(
        FocusUiState(
            durationMillis = DEFAULT_FOCUS_DURATION_MILLIS,
            remainingMillis = 0L,
            status = FocusTimerStatus.Completed,
        ),
    )
}

@Preview(
    name = "Compact portrait 320x568",
    group = "Adaptive layouts",
    showBackground = true,
    widthDp = 320,
    heightDp = 568,
)
@Composable
private fun CompactPortraitFocusScreenPreview() {
    FocusScreenPreview(FocusUiState())
}

@Preview(
    name = "Compact landscape 667x375",
    group = "Adaptive layouts",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 667,
    heightDp = 375,
)
@Composable
private fun CompactLandscapeFocusScreenPreview() {
    FocusScreenPreview(
        FocusUiState(
            remainingMillis = 12L * 60L * 1_000L,
            status = FocusTimerStatus.Running,
        ),
    )
}

@Preview(
    name = "Expanded 834x1194",
    group = "Adaptive layouts",
    showBackground = true,
    widthDp = 834,
    heightDp = 1194,
)
@Composable
private fun ExpandedFocusScreenPreview() {
    FocusScreenPreview(
        FocusUiState(
            durationMillis = LONG_FOCUS_DURATION_MILLIS,
            remainingMillis = 31L * 60L * 1_000L + 15L * 1_000L,
            status = FocusTimerStatus.Paused,
        ),
    )
}

@Preview(
    name = "Font scale 200%",
    group = "Accessibility",
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
    fontScale = 2f,
)
@Composable
private fun AccessibilityFocusScreenPreview() {
    FocusScreenPreview(
        FocusUiState(
            remainingMillis = 20L * 60L * 1_000L,
            status = FocusTimerStatus.Running,
        ),
    )
}

@Composable
private fun FocusScreenPreview(uiState: FocusUiState) {
    FocusLabTheme {
        FocusScreen(
            uiState = uiState,
            onAction = {},
        )
    }
}
