package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.titleM

/**
 * Universal Delete Confirmation Dialog used throughout the app (Item D6).
 * 20% enlarged width (374 dp), top danger icon, title, body message, and floated buttons
 * (Cancel on left, Delete on right).
 */
@Composable
fun AppDeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = "Delete",
    cancelText: String = "Cancel",
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape  = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppColor.surface),
            modifier = Modifier.width(374.dp).padding(Spacing.sm),
        ) {
            Column(
                modifier            = Modifier.padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Delete,
                    contentDescription = null,
                    tint               = AppColor.danger,
                    modifier           = Modifier.size(40.dp),
                )
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text      = title,
                    style     = titleM,
                    color     = AppColor.text,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text      = message,
                    style     = bodyM,
                    color     = AppColor.text2,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Spacing.xl))

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(
                        onClick = onDismiss,
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = AppColor.primaryContainer,
                            contentColor   = AppColor.onPrimaryContainer,
                        ),
                        shape   = RoundedCornerShape(14.dp),
                    ) {
                        Text(cancelText, style = labelL)
                    }

                    Button(
                        onClick = onConfirm,
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = AppColor.danger,
                            contentColor   = AppColor.onPrimary,
                        ),
                        shape   = RoundedCornerShape(14.dp),
                    ) {
                        Text(confirmText, style = labelL)
                    }
                }
            }
        }
    }
}
