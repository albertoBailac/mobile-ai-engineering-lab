package dev.albertobailac.focuslab.feature.focus

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.albertobailac.focuslab.ui.theme.FocusLabTheme

@Preview(
    name = "Light",
    group = "FocusLoopMark 16 24 48",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark",
    group = "FocusLoopMark 16 24 48",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
private annotation class LightDarkFocusLoopMarkPreviews

@LightDarkFocusLoopMarkPreviews
@Composable
private fun FocusLoopMarkScalePreview() {
    FocusLabTheme {
        Surface {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FocusLoopMark(size = 16.dp)
                FocusLoopMark(size = 24.dp)
                FocusLoopMark(size = 48.dp)
            }
        }
    }
}
