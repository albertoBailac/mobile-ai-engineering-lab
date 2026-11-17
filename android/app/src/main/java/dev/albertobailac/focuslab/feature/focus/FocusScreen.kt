package dev.albertobailac.focuslab.feature.focus

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.albertobailac.focuslab.R
import kotlin.math.ceil

private val ExpandedContentBreakpoint = 680.dp
private val CompactContentMaxWidth = 520.dp
private val ExpandedContentMaxWidth = 920.dp
private val ExpandedContentGap = 56.dp

@Composable
fun FocusScreen(
    uiState: FocusUiState,
    onAction: (FocusAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            val usefulWidth = (maxWidth - 48.dp).coerceAtLeast(0.dp)
            val usesExpandedLayout = usefulWidth >= ExpandedContentBreakpoint

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (usesExpandedLayout) {
                    val contentWidth = usefulWidth.coerceAtMost(ExpandedContentMaxWidth)
                    Row(
                        modifier = Modifier
                            .widthIn(max = ExpandedContentMaxWidth)
                            .fillMaxWidth()
                            .semantics { testTag = "focusContent" },
                        horizontalArrangement = Arrangement.spacedBy(ExpandedContentGap),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SessionConfiguration(
                            uiState = uiState,
                            onAction = onAction,
                            modifier = Modifier.weight(1f),
                        )
                        TimerSurface(
                            uiState = uiState,
                            onAction = onAction,
                            availableWidth = (contentWidth - ExpandedContentGap) / 2,
                            ringSize = 240.dp,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .widthIn(max = CompactContentMaxWidth)
                            .fillMaxWidth()
                            .semantics { testTag = "focusContent" },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        SessionConfiguration(uiState = uiState, onAction = onAction)
                        Spacer(Modifier.height(48.dp))
                        TimerSurface(
                            uiState = uiState,
                            onAction = onAction,
                            availableWidth = usefulWidth.coerceAtMost(CompactContentMaxWidth),
                            ringSize = 220.dp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionConfiguration(
    uiState: FocusUiState,
    onAction: (FocusAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FocusLoopMark()
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.focus_brand),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.6.sp,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.focus_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.focus_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))
        DurationSelector(
            selectedDurationMillis = uiState.durationMillis,
            enabled = uiState.status != FocusTimerStatus.Running,
            onSelect = { onAction(FocusAction.SelectDuration(it)) },
        )
    }
}

@Composable
private fun TimerSurface(
    uiState: FocusUiState,
    onAction: (FocusAction) -> Unit,
    availableWidth: Dp,
    ringSize: Dp,
    modifier: Modifier = Modifier,
) {
    val fontScale = LocalDensity.current.fontScale
    val usesAccessibleTimerLayout = fontScale >= 1.8f

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TimerDisplay(
                uiState = uiState,
                ringSize = ringSize,
                timeInsideRing = !usesAccessibleTimerLayout,
            )
            Spacer(Modifier.height(24.dp))
            StatusIndicator(uiState.status)
            Spacer(Modifier.height(28.dp))
            TimerActions(
                status = uiState.status,
                availableWidth = availableWidth - 48.dp,
                stackVertically = fontScale >= 1.8f,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun DurationSelector(
    selectedDurationMillis: Long,
    enabled: Boolean,
    onSelect: (Long) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FilterChip(
            selected = selectedDurationMillis == DEFAULT_FOCUS_DURATION_MILLIS,
            onClick = { onSelect(DEFAULT_FOCUS_DURATION_MILLIS) },
            label = { Text(stringResource(R.string.duration_25_minutes)) },
            enabled = enabled,
        )
        FilterChip(
            selected = selectedDurationMillis == LONG_FOCUS_DURATION_MILLIS,
            onClick = { onSelect(LONG_FOCUS_DURATION_MILLIS) },
            label = { Text(stringResource(R.string.duration_50_minutes)) },
            enabled = enabled,
        )
    }
}

@Composable
private fun TimerDisplay(
    uiState: FocusUiState,
    ringSize: Dp,
    timeInsideRing: Boolean,
) {
    val totalSeconds = ceil(uiState.remainingMillis / 1_000.0).toLong()
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    val formatted = "%02d:%02d".format(minutes, seconds)
    val spokenMinutes = pluralStringResource(
        R.plurals.minutes_remaining,
        minutes.toInt(),
        minutes,
    )
    val spokenSeconds = pluralStringResource(
        R.plurals.seconds_remaining,
        seconds.toInt(),
        seconds,
    )
    val spokenTime = stringResource(R.string.timer_accessibility_parts, spokenMinutes, spokenSeconds)
    val timerDescription = stringResource(R.string.timer_accessibility, spokenTime)
    val timerSemantics = Modifier.semantics {
        contentDescription = timerDescription
    }

    if (timeInsideRing) {
        Box(
            modifier = timerSemantics.size(ringSize),
            contentAlignment = Alignment.Center,
        ) {
            TimerProgress(uiState.progress, ringSize)
            TimerText(formatted)
        }
    } else {
        Column(
            modifier = timerSemantics,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(ringSize),
                contentAlignment = Alignment.Center,
            ) {
                TimerProgress(uiState.progress, ringSize)
            }
            Spacer(Modifier.height(16.dp))
            TimerText(formatted)
        }
    }
}

@Composable
private fun TimerProgress(progress: Float, ringSize: Dp) {
    CircularProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .size(ringSize)
            .clearAndSetSemantics {},
        strokeWidth = 12.dp,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
private fun TimerText(formatted: String) {
    Text(
        text = formatted,
        modifier = Modifier.clearAndSetSemantics { testTag = "timerText" },
        style = MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
    )
}

@Composable
private fun StatusIndicator(status: FocusTimerStatus) {
    val color = statusColor(status)
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) {
            if (status == FocusTimerStatus.Completed) {
                liveRegion = LiveRegionMode.Polite
            }
        },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(statusIcon(status)),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = statusLabel(status),
            style = MaterialTheme.typography.titleMedium,
            color = color,
        )
    }
}

@Composable
private fun TimerActions(
    status: FocusTimerStatus,
    availableWidth: Dp,
    stackVertically: Boolean,
    onAction: (FocusAction) -> Unit,
) {
    val useVerticalActions = stackVertically || availableWidth < 300.dp
    if (useVerticalActions) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ActionButtons(status, onAction, Modifier.fillMaxWidth())
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        ) {
            ActionButtons(status, onAction)
        }
    }
}

@Composable
private fun ActionButtons(
    status: FocusTimerStatus,
    onAction: (FocusAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (status) {
        FocusTimerStatus.Idle -> PrimaryAction(R.string.action_start, modifier) {
            onAction(FocusAction.Start)
        }
        FocusTimerStatus.Running -> PrimaryAction(R.string.action_pause, modifier) {
            onAction(FocusAction.Pause)
        }
        FocusTimerStatus.Paused -> PrimaryAction(R.string.action_resume, modifier) {
            onAction(FocusAction.Resume)
        }
        FocusTimerStatus.Completed -> Unit
    }

    if (status != FocusTimerStatus.Idle) {
        OutlinedButton(
            onClick = { onAction(FocusAction.Reset) },
            modifier = modifier,
        ) {
            Text(stringResource(R.string.action_reset))
        }
    }
}

@Composable
private fun PrimaryAction(
    labelResource: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(stringResource(labelResource))
    }
}

@Composable
private fun statusColor(status: FocusTimerStatus): Color = when (status) {
    FocusTimerStatus.Idle -> MaterialTheme.colorScheme.onSurfaceVariant
    FocusTimerStatus.Running -> MaterialTheme.colorScheme.primary
    FocusTimerStatus.Paused -> MaterialTheme.colorScheme.tertiary
    FocusTimerStatus.Completed -> MaterialTheme.colorScheme.secondary
}

@DrawableRes
private fun statusIcon(status: FocusTimerStatus): Int = when (status) {
    FocusTimerStatus.Idle -> R.drawable.ic_status_idle
    FocusTimerStatus.Running -> R.drawable.ic_status_running
    FocusTimerStatus.Paused -> R.drawable.ic_status_paused
    FocusTimerStatus.Completed -> R.drawable.ic_status_completed
}

@Composable
private fun statusLabel(status: FocusTimerStatus): String = stringResource(
    when (status) {
        FocusTimerStatus.Idle -> R.string.timer_idle
        FocusTimerStatus.Running -> R.string.timer_running
        FocusTimerStatus.Paused -> R.string.timer_paused
        FocusTimerStatus.Completed -> R.string.timer_completed
    },
)
