package sabuj.m.myevents.core.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing

/**
 * Render any [EventIcon] — displays [EventIcon.customRes] (PNG category drawable) if available,
 * falling back to [EventIcon.fallback] vector image.
 */
@Composable
fun EventIconDisplay(
    icon: EventIcon,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = AppColor.primary,
) {
    if (icon.customRes != null) {
        Image(
            painter            = painterResource(icon.customRes),
            contentDescription = contentDescription,
            modifier           = modifier,
            contentScale       = ContentScale.Fit,
        )
    } else {
        Icon(
            imageVector        = icon.fallback,
            contentDescription = contentDescription,
            tint               = tint,
            modifier           = modifier,
        )
    }
}

/**
 * Default event icon — 24 × 24 dp tinted icon for event-related UI.
 */
@Composable
fun EventIcon(
    modifier: Modifier = Modifier,
    tint: Color = AppColor.primary,
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
