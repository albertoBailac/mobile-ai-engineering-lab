package dev.albertobailac.focuslab.feature.focus

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.albertobailac.focuslab.R

@Composable
internal fun FocusLoopMark(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = colorResource(R.color.action_primary),
) {
    Icon(
        painter = painterResource(R.drawable.ic_focus_loop),
        contentDescription = null,
        modifier = modifier.size(size),
        tint = tint,
    )
}
