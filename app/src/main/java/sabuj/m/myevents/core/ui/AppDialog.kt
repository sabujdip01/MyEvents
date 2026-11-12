package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape    = RoundedCornerShape(Radius.dialog),
            colors   = CardDefaults.cardColors(containerColor = AppColor.surface),
            modifier = modifier.width(374.dp).padding(Spacing.sm),
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(title, style = titleM, color = AppColor.text)
                Text(body, style = bodyM, color = AppColor.text2)
                Spacer(Modifier.height(Spacing.xs))
                AppButtonRow(
                    positiveText = confirmText,
                    onPositive   = onConfirm,
                    negativeText = dismissText,
                    onNegative   = onDismiss,
                    isDangerous  = isDangerous,
                )
            }
        }
    }
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
