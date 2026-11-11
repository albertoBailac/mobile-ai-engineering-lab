package dev.albertobailac.focuslab.feature.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.albertobailac.focuslab.R
import kotlin.math.ceil

@Composable
fun FocusScreen(
    uiState: FocusUiState,
    onAction: (FocusAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.focus_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
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

            Spacer(Modifier.height(48.dp))
            TimerDisplay(uiState)
            Spacer(Modifier.height(32.dp))
            Text(
                text = statusLabel(uiState.status),
                modifier = Modifier.semantics {
                    liveRegion = LiveRegionMode.Polite
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))

            TimerActions(uiState.status, onAction)
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
private fun TimerDisplay(uiState: FocusUiState) {
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

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier
                .size(180.dp)
                .semantics { contentDescription = timerDescription },
            strokeWidth = 12.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = formatted,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun TimerActions(
    status: FocusTimerStatus,
    onAction: (FocusAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
    ) {
        when (status) {
            FocusTimerStatus.Idle -> PrimaryAction(R.string.action_start) {
                onAction(FocusAction.Start)
            }

            FocusTimerStatus.Running -> PrimaryAction(R.string.action_pause) {
                onAction(FocusAction.Pause)
            }

            FocusTimerStatus.Paused -> PrimaryAction(R.string.action_resume) {
                onAction(FocusAction.Resume)
            }

            FocusTimerStatus.Completed -> Unit
        }

        if (status != FocusTimerStatus.Idle) {
            OutlinedButton(onClick = { onAction(FocusAction.Reset) }) {
                Text(stringResource(R.string.action_reset))
            }
        }
    }
}

@Composable
private fun PrimaryAction(
    labelResource: Int,
    onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        Text(stringResource(labelResource))
    }
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

