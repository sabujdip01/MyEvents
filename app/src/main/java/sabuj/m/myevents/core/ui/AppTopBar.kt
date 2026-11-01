package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.titleL

/**
 * Standard top bar for all screens in MyEvents.
 * Places the navigation menu icon (2.5 lines) on the left and the screen/app title
 * immediately to its right, aligned at the top.
 */
@Composable
fun AppTopBar(
    titleText: String,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    titleContent: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = Spacing.sm, bottom = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onMenuClick != null) {
            MenuButton(onClick = onMenuClick)
            Spacer(Modifier.width(Spacing.md))
        }

        Box(modifier = Modifier.weight(1f)) {
            if (titleContent != null) {
                titleContent()
            } else {
                Text(
                    text  = titleText,
                    style = titleL,
                    color = AppColor.text,
                )
            }
        }

        if (actions != null) {
            actions()
        }
    }
}
