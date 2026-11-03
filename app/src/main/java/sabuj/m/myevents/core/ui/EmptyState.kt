package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.titleM

/**
 * Full-width empty state placeholder.
 * Show this when a list has no items, with an icon, title and optional subtitle.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
) {
    Column(
        modifier              = modifier
            .fillMaxWidth()
            .padding(Spacing.xl3),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center,
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = AppColor.text3,
            modifier           = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(Spacing.md))
        Text(
            text      = title,
            style     = titleM,
            color     = AppColor.text2,
            textAlign = TextAlign.Center,
        )
        if (subtitle.isNotEmpty()) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text      = subtitle,
                style     = bodyM,
                color     = AppColor.text3,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@LightDarkPreviews
@Composable
private fun EmptyStatePreview() {
    AppTheme {
        EmptyState(
            icon     = sabuj.m.myevents.core.icons.AppIcon.Events.fallback,
            title    = "No events yet",
            subtitle = "Tap + to add your first event",
        )
    }
}

