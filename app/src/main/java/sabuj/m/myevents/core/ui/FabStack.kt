package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing

/**
 * Vertical stack of [AppFab]s anchored to the bottom-right corner.
 * When a screen needs more than one FAB action, wrap them with [FabStack].
 *
 * @param actions list of (icon, contentDescription, onClick) triples, top-to-bottom.
 */
@Composable
fun FabStack(
    actions: List<Triple<ImageVector, String, () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier
            .navigationBarsPadding()
            .padding(bottom = Spacing.lg, end = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        horizontalAlignment = Alignment.End,
    ) {
        actions.forEach { (icon, desc, onClick) ->
            AppFab(
                onClick            = onClick,
                icon               = icon,
                contentDescription = desc,
                // Remove the built-in padding — FabStack provides its own
                modifier           = Modifier,
            )
        }
    }
}

@LightDarkPreviews
@Composable
private fun FabStackPreview() {
    AppTheme {
        FabStack(
            actions = listOf(
                Triple(Icons.Rounded.Add, "Add", {}),
                Triple(Icons.Rounded.FileUpload, "Export", {}),
            ),
        )
    }
}

