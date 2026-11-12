package sabuj.m.myevents.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL

/**
 * App-wide snackbar/toast host.
 * Positioned at middle of screen, 25% reduced width (75% screen width),
 * text and button in same line, 5-second auto-dismiss.
 * Toast height is fixed to 62dp so SyncFeedbackToast and all toasts match exactly in height, width, and colors.
 * SyncFeedbackToast has actionLabel = null, so no button is rendered.
 * Toast BG & text = same as muted pill (`surfaceVariant` & `text2`).
 * Button BG & text = same as selected pill (`primaryContainer` & `onPrimaryContainer`).
 */
@Composable
fun AppSnackbar(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier  = modifier,
        snackbar  = { data ->
            AppSnackbarItem(data)
        },
    )
}

/** Single toast/snackbar item appearance — uniform height and styling for all toasts. */
@Composable
private fun AppSnackbarItem(data: SnackbarData, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth(0.75f) // Width reduced by 25%
            .height(62.dp),      // Fixed 62dp height so all toasts (with or without buttons) match exactly
        shape = RoundedCornerShape(Radius.field), // Normal button/field corner radius (14dp)
        color = AppColor.surfaceVariant, // Toast BG = muted pill BG
        shadowElevation = 6.dp,
        border = BorderStroke(
            0.8.dp,
            if (AppColor.isDark) AppColor.outlineVariant else AppColor.outlineVariant.copy(alpha = 0.5f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text      = data.visuals.message,
                style     = bodyM, // 14sp Normal
                color     = AppColor.text2, // Toast text = muted pill text
                textAlign = TextAlign.Center,
                modifier  = Modifier.weight(1f),
            )

            data.visuals.actionLabel?.let { label ->
                Spacer(Modifier.width(Spacing.xs))
                Surface(
                    onClick = { data.performAction() },
                    shape   = RoundedCornerShape(Radius.field), // Normal button border radius (14dp)
                    color   = AppColor.primaryContainer, // Button BG = selected pill BG
                ) {
                    Box(
                        modifier         = Modifier.padding(horizontal = 14.dp, vertical = 9.dp), // Button height +50%
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text  = label,
                            style = labelL, // 14sp SemiBold
                            color = AppColor.onPrimaryContainer, // Button text = selected pill text
                        )
                    }
                }
            }
        }
    }
}

@LightDarkPreviews
@Composable
private fun AppSnackbarWithButtonPreview() {
    AppTheme {
        AppSnackbarItem(
            data = object : SnackbarData {
                override val visuals = object : androidx.compose.material3.SnackbarVisuals {
                    override val actionLabel       = "Undo"
                    override val duration          = androidx.compose.material3.SnackbarDuration.Short
                    override val message           = "Event deleted"
                    override val withDismissAction = false
                }
                override fun dismiss()       {}
                override fun performAction() {}
            }
        )
    }
}

@LightDarkPreviews
@Composable
private fun FirebaseSyncToastPreview() {
    AppTheme {
        AppSnackbarItem(
            data = object : SnackbarData {
                override val visuals = object : androidx.compose.material3.SnackbarVisuals {
                    override val actionLabel       = null
                    override val duration          = androidx.compose.material3.SnackbarDuration.Short
                    override val message           = "Sync completed successfully"
                    override val withDismissAction = false
                }
                override fun dismiss()       {}
                override fun performAction() {}
            }
        )
    }
}
