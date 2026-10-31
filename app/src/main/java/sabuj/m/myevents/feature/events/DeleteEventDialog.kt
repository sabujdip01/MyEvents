package sabuj.m.myevents.feature.events

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.domain.model.Event

/**
 * Delete confirmation dialog shown before soft-deleting an event.
 * Warns that linked bookings, expenses, invitees and tasks will also be removed.
 */
@Composable
fun DeleteEventDialog(
    event: Event,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = AppColor.surface,
        shape            = RoundedCornerShape(28.dp),
        icon = {
            Icon(
                imageVector        = Icons.Rounded.Delete,
                contentDescription = null,
                tint               = AppColor.danger,
                modifier           = Modifier.size(32.dp),
            )
        },
        title = {
            Text(
                text  = "Delete event?",
                style = titleM,
                color = AppColor.text,
            )
        },
        text = {
            Text(
                text  = "\"${event.title}\" and all its linked bookings, expenses, invitees and tasks will be removed. You have 10 seconds to undo.",
                style = bodyM,
                color = AppColor.text2,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = AppColor.danger,
                    contentColor   = AppColor.onPrimary,
                ),
                shape   = RoundedCornerShape(12.dp),
            ) {
                Text("Delete", style = labelL)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = AppColor.primaryContainer,
                    contentColor   = AppColor.onPrimaryContainer,
                ),
                shape   = RoundedCornerShape(12.dp),
            ) {
                Text("Cancel", style = labelL)
            }
        },
    )
}

