package sabuj.m.myevents.feature.bookings

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.ui.AppButtonRow
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
import java.time.LocalDate
import java.util.Calendar

/**
 * Dialog for adding a payment to a booking.
 * Shows a date field, amount field and note field.
 *
 * @param state          Current [PaymentDialogState.Add].
 * @param onDismiss      Close the dialog without saving.
 * @param onDateChange   Update the date epoch day.
 * @param onAmountChange Update the amount in paise.
 * @param onNoteChange   Update the note text.
 * @param onSave         Save the payment.
 */
@Composable
fun PaymentDialog(
    state: PaymentDialogState.Add,
    onDismiss: () -> Unit,
    onDateChange: (Long) -> Unit,
    onAmountChange: (Long) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    val context = LocalContext.current
    val date = LocalDate.ofEpochDay(state.dateEpochDay)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = AppColor.surface,
        shape            = RoundedCornerShape(Radius.dialog),
        title = {
            Text(
                text  = "Add Payment", // TODO strings.xml
                style = titleM,
                color = AppColor.text,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                // Date picker field
                OutlinedTextField(
                    value         = DateUtils.formatDisplay(date),
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Date", style = bodyM) }, // TODO strings.xml
                    trailingIcon  = {
                        Icon(
                            imageVector        = Icons.Rounded.DateRange,
                            contentDescription = "Pick Date",
                            tint               = AppColor.primary,
                            modifier           = Modifier.clickable {
                                val cal = Calendar.getInstance()
                                cal.set(date.year, date.monthValue - 1, date.dayOfMonth)
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        onDateChange(LocalDate.of(year, month + 1, day).toEpochDay())
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH),
                                ).show()
                            },
                        )
                    },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(Radius.field),
                    colors        = paymentFieldColors(),
                )

                // Amount field
                OutlinedTextField(
                    value         = if (state.amountPaise == 0L) "" else (state.amountPaise / 100L).toString(),
                    onValueChange = { text -> onAmountChange(text.toLongOrNull()?.times(100L) ?: 0L) },
                    label         = { Text("Amount (₹)", style = bodyM) }, // TODO strings.xml
                    placeholder   = { Text("0", style = bodyM, color = AppColor.text3) },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape         = RoundedCornerShape(Radius.field),
                    isError       = state.amountError != null,
                    supportingText = if (state.amountError != null) {
                        { Text(state.amountError, style = bodyM) }
                    } else null,
                    colors        = paymentFieldColors(),
                )

                // Note field
                OutlinedTextField(
                    value         = state.note,
                    onValueChange = onNoteChange,
                    label         = { Text("Note (Optional)", style = bodyM) }, // TODO strings.xml
                    placeholder   = { Text("e.g. Advance Payment", style = bodyM, color = AppColor.text3) },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    shape         = RoundedCornerShape(Radius.field),
                    colors        = paymentFieldColors(),
                )
            }
        },
        confirmButton = {},
        dismissButton = {},
        // Override buttons to use AppButtonRow
    )
}

/**
 * Full payment dialog with proper button row.
 * Wraps [PaymentDialog] with the [AppButtonRow].
 */
@Composable
fun PaymentDialogFull(
    state: PaymentDialogState.Add,
    onDismiss: () -> Unit,
    onDateChange: (Long) -> Unit,
    onAmountChange: (Long) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    val context = LocalContext.current
    val date = LocalDate.ofEpochDay(state.dateEpochDay)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape    = RoundedCornerShape(Radius.dialog),
            colors   = CardDefaults.cardColors(containerColor = AppColor.surface),
            modifier = Modifier.width(374.dp).padding(Spacing.sm),
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    text  = "Add Payment",
                    style = titleM,
                    color = AppColor.text,
                )

                // Date picker field
                OutlinedTextField(
                    value         = DateUtils.formatDisplay(date),
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Date", style = bodyM) },
                    trailingIcon  = {
                        Icon(
                            imageVector        = Icons.Rounded.DateRange,
                            contentDescription = "Pick Date",
                            tint               = AppColor.primary,
                            modifier           = Modifier.clickable {
                                val cal = Calendar.getInstance()
                                cal.set(date.year, date.monthValue - 1, date.dayOfMonth)
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        onDateChange(LocalDate.of(year, month + 1, day).toEpochDay())
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH),
                                ).show()
                            },
                        )
                    },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(Radius.field),
                    colors        = paymentFieldColors(),
                )

                // Amount field
                OutlinedTextField(
                    value         = if (state.amountPaise == 0L) "" else (state.amountPaise / 100L).toString(),
                    onValueChange = { text -> onAmountChange(text.toLongOrNull()?.times(100L) ?: 0L) },
                    label         = { Text("Amount (₹)", style = bodyM) },
                    placeholder   = { Text("0", style = bodyM, color = AppColor.text3) },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape         = RoundedCornerShape(Radius.field),
                    isError       = state.amountError != null,
                    supportingText = if (state.amountError != null) {
                        { Text(state.amountError, style = bodyM) }
                    } else null,
                    colors        = paymentFieldColors(),
                )

                // Note field
                OutlinedTextField(
                    value         = state.note,
                    onValueChange = onNoteChange,
                    label         = { Text("Note (Optional)", style = bodyM) },
                    placeholder   = { Text("e.g. Advance Payment", style = bodyM, color = AppColor.text3) },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    shape         = RoundedCornerShape(Radius.field),
                    colors        = paymentFieldColors(),
                )

                Spacer(Modifier.height(Spacing.xs))

                AppButtonRow(
                    positiveText = "Save",
                    onPositive   = onSave,
                    negativeText = "Cancel",
                    onNegative   = onDismiss,
                    positiveEnabled = state.amountPaise > 0,
                )
            }
        }
    }
}

@Composable
private fun paymentFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = AppColor.primary,
    unfocusedBorderColor = AppColor.outline,
    focusedLabelColor    = AppColor.primary,
    unfocusedLabelColor  = AppColor.text2,
    cursorColor          = AppColor.primary,
    errorBorderColor     = AppColor.danger,
)

@sabuj.m.myevents.core.preview.LightDarkPreviews
@Composable
private fun PaymentDialogPreview() {
    AppTheme {
        PaymentDialogFull(
            state          = PaymentDialogState.Add(bookingId = "bk-1", amountPaise = 5_000_00L, note = "Advance Payment"),
            onDismiss      = {},
            onDateChange   = {},
            onAmountChange = {},
            onNoteChange   = {},
            onSave         = {},
        )
    }
}
