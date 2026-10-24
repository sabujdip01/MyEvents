package sabuj.m.myevents.feature.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.icons.AppIcon
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.ui.ScreenScaffold

/**
 * Temporary placeholder screen shown for routes not yet built.
 * Displays the screen title and a centred empty-state hint.
 * Replaced screen by screen in Phases 2–8.
 */
@Composable
fun PlaceholderScreen(
    title: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenScaffold(modifier = modifier, onMenuClick = onMenuClick) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector        = AppIcon.Events.fallback,
                    contentDescription = null,
                    tint               = AppColor.text3,
                    modifier           = Modifier.size(64.dp),
                )
                Spacer(Modifier.height(Spacing.lg))
                Text(title, style = titleL, color = AppColor.text)
                Spacer(Modifier.height(Spacing.sm))
                Text("Coming soon", style = bodyM, color = AppColor.text2)
            }
        }
    }
}

@LightDarkPreviews
@Composable
private fun PlaceholderScreenPreview() {
    AppTheme {
        PlaceholderScreen(title = "Bookings", onMenuClick = {})
    }
}
