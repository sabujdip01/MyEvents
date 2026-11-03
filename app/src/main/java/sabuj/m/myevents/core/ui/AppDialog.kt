package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.titleM

/**
 * App-wide dialog with title, body text, and confirm/dismiss buttons.
 * Use [AppDeleteDialog] (core/ui/AppDeleteDialog.kt) for destructive confirmations.
 */
@Composable
fun AppDialog(
    title: String,
    body: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String = "Cancel",
    isDangerous: Boolean = false,
) {
    AlertDialog(
        onDismissRequest  = onDismiss,
        title             = { Text(title, style = titleM, color = AppColor.text) },
        text              = { Text(body, style = bodyM, color = AppColor.text2) },
        shape             = RoundedCornerShape(Radius.dialog),
        containerColor    = AppColor.surface,
        confirmButton     = {
            if (isDangerous) {
                AppButtonDanger(text = confirmText, onClick = onConfirm)
            } else {
                AppButton(text = confirmText, onClick = onConfirm)
            }
        },
        dismissButton     = { AppButtonText(text = dismissText, onClick = onDismiss) },
        modifier          = modifier,
    )
}

@LightDarkPreviews
@Composable
private fun AppDialogPreview() {
    AppTheme {
        AppDialog(
            title       = "Confirm Action",
            body        = "Are you sure you want to do this?",
            confirmText = "Confirm",
            onConfirm   = {},
            onDismiss   = {},
        )
    }
}

