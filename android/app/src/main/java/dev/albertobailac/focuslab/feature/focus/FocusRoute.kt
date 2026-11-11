package dev.albertobailac.focuslab.feature.focus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FocusRoute(
    viewModel: FocusViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveLifecycle(viewModel::onAction)
    FocusScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun ObserveLifecycle(onAction: (FocusAction) -> Unit) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val currentOnAction by rememberUpdatedState(onAction)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> currentOnAction(FocusAction.AppResumed)
                Lifecycle.Event.ON_STOP -> currentOnAction(FocusAction.AppBackgrounded)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            currentOnAction(FocusAction.AppResumed)
        }
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

