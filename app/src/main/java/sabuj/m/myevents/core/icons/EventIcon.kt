package sabuj.m.myevents.core.icons

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing

/**
 * Event icon — 24 × 24 dp tinted icon for event-related UI.
 * Currently uses the Material [CalendarMonth] fallback.
 * TODO custom svg: replace with the duotone SVG from design/svg/ev_event.svg
 * once the SVG has been converted to a Compose vector drawable.
 *
 * @param modifier applied to the [Icon].
 * @param tint     colour override; defaults to [AppColor.primary].
 */
@Composable
fun EventIcon(
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = AppColor.primary,
) {
    Icon(
        imageVector        = Icons.Rounded.CalendarMonth,
        contentDescription = "Event",
        tint               = tint,
        modifier           = modifier.size(24.dp),
    )
}

@LightDarkPreviews
@Composable
private fun EventIconPreview() {
    AppTheme {
        EventIcon(modifier = Modifier.padding(Spacing.lg))
    }
}

