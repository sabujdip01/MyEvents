package sabuj.m.myevents.core.ui

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL

/**
 * App-wide snackbar host. Drop this into a [Scaffold]'s snackbarHost slot
 * with a shared [SnackbarHostState] so snackbars look consistent everywhere.
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

/** Single snackbar appearance — surface background with primary action colour. */
@Composable
private fun AppSnackbarItem(data: SnackbarData) {
    Snackbar(
        action = data.visuals.actionLabel?.let { label ->
            {
                TextButton(onClick = { data.performAction() }) {
                    Text(label, style = labelL, color = AppColor.primary)
                }
            }
        },
        containerColor = AppColor.surface,
        contentColor   = AppColor.text,
        actionOnNewLine = false,
    ) {
        Text(data.visuals.message, style = bodyM)
    }
}

@LightDarkPreviews
@Composable
private fun AppSnackbarPreview() {
    AppTheme {
        // Preview shows the snackbar item directly since host needs a coroutine scope
        AppSnackbarItem(
            data = object : SnackbarData {
                override val visuals = object : androidx.compose.material3.SnackbarVisuals {
                    override val actionLabel   = "Undo"
                    override val duration      = androidx.compose.material3.SnackbarDuration.Short
                    override val message       = "Event deleted"
                    override val withDismissAction = false
                }
                override fun dismiss()       {}
                override fun performAction() {}
            }
        )
    }
}

