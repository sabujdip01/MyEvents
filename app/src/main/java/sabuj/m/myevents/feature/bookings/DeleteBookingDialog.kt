package sabuj.m.myevents.feature.bookings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.ui.AppButtonRow
import sabuj.m.myevents.core.ui.IconChipDanger
import sabuj.m.myevents.core.ui.IconChipSize

/**
 * Confirmation dialog shown before deleting a booking.
 * Warns the user that all payments will also be removed.
 *
 * @param bookingTitle The title of the booking being deleted.
 * @param onDismiss    Called when the user cancels.
 * @param onConfirm   Called when the user confirms the delete.
 */
@Composable
fun DeleteBookingDialog(
    bookingTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = AppColor.surface,
        shape            = RoundedCornerShape(Radius.dialog),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                IconChipDanger(
                    icon               = Icons.Rounded.Delete,
                    contentDescription = null,
                    size               = IconChipSize.Lg,
                )
                Text(
                    text  = "Delete booking?", // TODO strings.xml
                    style = titleM,
                    color = AppColor.text,
                )
            }
        },
        text = {
            Column {
                Text(
                    text  = "\"$bookingTitle\" and all its payments will be permanently removed.", // TODO strings.xml
                    style = bodyM,
                    color = AppColor.text2,
                )
                Spacer(Modifier.height(Spacing.lg))
                AppButtonRow(
                    positiveText = "Delete", // TODO strings.xml
                    onPositive   = onConfirm,
                    negativeText = "Cancel", // TODO strings.xml
                    onNegative   = onDismiss,
                    isDangerous  = true,
                )
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}
