package sabuj.m.myevents.feature.bookings

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.labelS
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.ui.AppButtonRow
import sabuj.m.myevents.core.ui.IconChip
import sabuj.m.myevents.core.ui.IconChipSize
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.domain.model.BookingPayment
import sabuj.m.myevents.domain.model.Event
import java.time.LocalDate
import java.util.Calendar

/**
 * Bottom sheet for adding or editing a booking.
 * In Edit mode, shows the payment log and an "Add payment" button.
 *
 * @param sheetState      Current [BookingSheetState.Add] or [BookingSheetState.Edit].
 * @param events          All events for the event dropdown.
 * @param onDismiss       Called when the sheet should close.
 * @param onTypeChange    Update the booking type field.
 * @param onIconChange    Update the selected booking icon.
 * @param onEventChange   Update the selected event id.
 * @param onContactChange Update the contact name field.
 * @param onPhoneChange   Update the phone field.
 * @param onTotalChange   Update the total amount in paise.
 * @param onDueDateChange Update the next due date epoch day (nullable).
 * @param onSave          Called when the user taps Save.
 * @param onAddPayment    Called when the user taps "Add payment" in Edit mode.
 * @param onDeletePayment Called when the user taps the delete icon on a payment row.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditBookingSheet(
    sheetState: BookingSheetState,
    events: List<Event>,
    onDismiss: () -> Unit,
    onTypeChange: (String) -> Unit,
    onIconChange: (BookingIcon) -> Unit,
    onEventChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onTotalChange: (Long) -> Unit,
    onBookingAmountChange: (Long) -> Unit = {},
    onDueDateChange: (Long?) -> Unit,
    onSave: () -> Unit,
    onAddPayment: () -> Unit,
    onDeletePayment: (String) -> Unit,
) {
    val isEdit = sheetState is BookingSheetState.Edit
    val bookingType: String
    val bookingIcon: BookingIcon
    val eventId: String
    val contactName: String
    val phone: String
    val totalAmountPaise: Long
    val bookingAmountPaise: Long
    val nextDueDateEpochDay: Long?
    val eventError: String?
    val totalError: String?
    val payments: List<BookingPayment>

    when (sheetState) {
        is BookingSheetState.Add -> {
            bookingType = sheetState.bookingType
            bookingIcon = sheetState.bookingIcon; eventId = sheetState.eventId
            contactName = sheetState.contactName; phone = sheetState.phone
            totalAmountPaise = sheetState.totalAmountPaise
            bookingAmountPaise = sheetState.bookingAmountPaise
            nextDueDateEpochDay = sheetState.nextDueDateEpochDay
            eventError = sheetState.eventError
            totalError = sheetState.totalError; payments = emptyList()
        }
        is BookingSheetState.Edit -> {
            bookingType = sheetState.bookingType
            bookingIcon = sheetState.bookingIcon; eventId = sheetState.eventId
            contactName = sheetState.contactName; phone = sheetState.phone
            totalAmountPaise = sheetState.totalAmountPaise
            bookingAmountPaise = sheetState.bookingAmountPaise
            nextDueDateEpochDay = sheetState.nextDueDateEpochDay
            eventError = sheetState.eventError
            totalError = sheetState.totalError; payments = sheetState.payments
        }
        else -> return // Hidden â€” nothing to show
    }

    val paidPaise = payments.sumOf { it.amountPaise }
    val amountLeftPaise = totalAmountPaise - paidPaise
    val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = modalSheetState,
        containerColor   = AppColor.surface,
        shape            = RoundedCornerShape(topStart = Radius.sheet, topEnd = Radius.sheet),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            // Sheet title
            Text(
                text  = if (isEdit) "Edit booking" else "Add booking", // TODO strings.xml
                style = titleL,
                color = AppColor.text,
            )


            // Booking type field
            BookingTextField(
                value         = bookingType,
                onValueChange = onTypeChange,
                label         = "Booking name / type", // TODO strings.xml
                placeholder   = "e.g. Camera, Mandap",
            )

            // Booking icon picker
            Text(text = "Booking icon", style = labelM, color = AppColor.text2) // TODO strings.xml
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement   = Arrangement.spacedBy(Spacing.sm),
            ) {
                BookingIcon.entries.forEach { icon ->
                    val isSelected = icon == bookingIcon
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(Radius.field))
                            .background(if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant)
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) AppColor.primary else AppColor.surface,
                                shape = RoundedCornerShape(Radius.field),
                            )
                            .clickable { onIconChange(icon) },
                        contentAlignment = Alignment.Center,
                    ) {
                        IconChip(
                            icon               = icon.fallback,
                            contentDescription = icon.name,
                            size               = IconChipSize.Default,
                            containerColor     = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant,
                            iconColor          = if (isSelected) AppColor.primary else AppColor.text2,
                        )
                    }
                }
            }

            // For event dropdown
            EventDropdown(
                events    = events,
                selectedId = eventId,
                onSelect  = onEventChange,
                isError   = eventError != null,
                errorText = eventError,
            )

            // Contact person
            BookingTextField(
                value         = contactName,
                onValueChange = onContactChange,
                label         = "Contact person", // TODO strings.xml
                placeholder   = "Vendor name",
            )

            // Phone
            BookingTextField(
                value         = phone,
                onValueChange = onPhoneChange,
                label         = "Phone", // TODO strings.xml
                placeholder   = "10-digit number",
                keyboardType  = KeyboardType.Phone,
            )

            // Contract Amount
            val contractRupeesStr = if (totalAmountPaise == 0L) "" else formatIndianDigits((totalAmountPaise / 100L).toString())
            BookingMoneyTextField(
                value         = contractRupeesStr,
                onValueChange = { text ->
                    val digits = text.filter { it.isDigit() }
                    onTotalChange(digits.toLongOrNull()?.times(100L) ?: 0L)
                },
                label         = "Contract Amount (₹)",
                placeholder   = "0",
                isError       = totalError != null,
                supportingText = totalError,
            )

            // Booking Amount / Advance (only in Add mode)
            if (!isEdit) {
                val bookingRupeesStr = if (bookingAmountPaise == 0L) "" else formatIndianDigits((bookingAmountPaise / 100L).toString())
                BookingMoneyTextField(
                    value         = bookingRupeesStr,
                    onValueChange = { text ->
                        val digits = text.filter { it.isDigit() }
                        onBookingAmountChange(digits.toLongOrNull()?.times(100L) ?: 0L)
                    },
                    label         = "Booking Amount / Advance (₹)",
                    placeholder   = "0",
                )
            }

            // Next due date (optional)
            val dueDateLabel = nextDueDateEpochDay?.let { DateUtils.formatDisplay(LocalDate.ofEpochDay(it)) } ?: "Not set"
            BookingTextField(
                value         = dueDateLabel,
                onValueChange = {},
                label         = "Next due date (optional)", // TODO strings.xml
                readOnly      = true,
                trailingIcon  = {
                    Icon(
                        imageVector        = Icons.Rounded.DateRange,
                        contentDescription = "Pick date",
                        tint               = AppColor.primary,
                        modifier           = Modifier.clickable {
                            val cal = Calendar.getInstance()
                            nextDueDateEpochDay?.let { epochDay ->
                                val d = LocalDate.ofEpochDay(epochDay)
                                cal.set(d.year, d.monthValue - 1, d.dayOfMonth)
                            }
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    onDueDateChange(LocalDate.of(year, month + 1, day).toEpochDay())
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH),
                            ).show()
                        },
                    )
                },
            )

            // ── Payment log (Edit mode only) ──────────────────────────
            if (isEdit) {
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text  = "PAYMENTS", // TODO strings.xml
                    style = labelS,
                    color = AppColor.text2,
                )
                // Read-only paid total
                BookingTextField(
                    value         = MoneyFormatter.format(paidPaise),
                    onValueChange = {},
                    label         = "Paid so far", // TODO strings.xml
                    readOnly      = true,
                )
                // Read-only amount left
                BookingTextField(
                    value         = MoneyFormatter.format(amountLeftPaise),
                    onValueChange = {},
                    label         = "Remaining Amount", // TODO strings.xml
                    readOnly      = true,
                )
                // Payment rows
                payments.forEach { payment ->
                    PaymentRow(
                        payment       = payment,
                        onDeleteClick = { onDeletePayment(payment.id) },
                    )
                }
                // Add payment button
                TextButton(
                    onClick   = onAddPayment,
                    modifier  = Modifier.fillMaxWidth(),
                ) {
                    Text("+ Add payment", style = labelL, color = AppColor.primary) // TODO strings.xml
                }
            }

            // â”€â”€ Action buttons â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
            val isSaveEnabled = bookingType.isNotBlank() && eventId.isNotBlank() && totalAmountPaise > 0
            AppButtonRow(
                positiveText    = if (isEdit) "Save" else "Add", // TODO strings.xml
                onPositive      = onSave,
                negativeText    = "Cancel", // TODO strings.xml
                onNegative      = onDismiss,
                positiveEnabled = isSaveEnabled,
            )

            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

/** A single payment row in the payment log. */
@Composable
private fun PaymentRow(
    payment: BookingPayment,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = DateUtils.formatDisplay(payment.date),
                style = labelM,
                color = AppColor.text,
            )
            if (payment.note.isNotBlank()) {
                Text(
                    text  = payment.note,
                    style = bodyM,
                    color = AppColor.text2,
                )
            }
        }
        Text(
            text  = MoneyFormatter.format(payment.amountPaise),
            style = labelL,
            color = AppColor.text,
        )
        Spacer(Modifier.width(Spacing.sm))
        Icon(
            imageVector        = Icons.Rounded.Delete,
            contentDescription = "Delete payment",
            tint               = AppColor.danger,
            modifier           = Modifier
                .size(20.dp)
                .clickable { onDeleteClick() },
        )
    }
}

/** Event dropdown for the for-event selector. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventDropdown(
    events: List<Event>,
    selectedId: String,
    onSelect: (String) -> Unit,
    isError: Boolean = false,
    errorText: String? = null,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedEvent = events.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded        = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier        = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value              = selectedEvent?.let { "${it.title} (${it.eventName})" } ?: "",
            onValueChange      = {},
            readOnly           = true,
            label              = { Text("For event", style = bodyM) }, // TODO strings.xml
            trailingIcon       = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier           = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            isError            = isError,
            supportingText     = if (errorText != null) { { Text(errorText, style = bodyM) } } else null,
            shape              = RoundedCornerShape(Radius.field),
            colors             = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppColor.primary,
                unfocusedBorderColor = AppColor.outline,
                focusedLabelColor    = AppColor.primary,
                unfocusedLabelColor  = AppColor.text2,
                cursorColor          = AppColor.primary,
                errorBorderColor     = AppColor.danger,
            ),
        )
        ExposedDropdownMenu(
            expanded        = expanded,
            onDismissRequest = { expanded = false },
        ) {
            events.forEach { event ->
                DropdownMenuItem(
                    text    = { Text("${event.title} (${event.eventName})", style = bodyM) },
                    onClick = { onSelect(event.id); expanded = false },
                    leadingIcon = {
                        Icon(
                            imageVector        = event.icon.fallback,
                            contentDescription = null,
                            tint               = AppColor.primary,
                            modifier           = Modifier.size(20.dp),
                        )
                    },
                )
            }
        }
    }
}

/** Reusable styled text field for the booking sheet. */
@Composable
private fun BookingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label, style = bodyM) },
        placeholder    = { Text(placeholder, style = bodyM, color = AppColor.text3) },
        modifier       = modifier.fillMaxWidth(),
        readOnly       = readOnly,
        trailingIcon   = trailingIcon,
        singleLine     = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape          = RoundedCornerShape(Radius.field),
        isError        = isError,
        supportingText = if (supportingText != null) { { Text(supportingText, style = bodyM) } } else null,
        colors         = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColor.primary,
            unfocusedBorderColor = AppColor.outline,
            focusedLabelColor    = AppColor.primary,
            unfocusedLabelColor  = AppColor.text2,
            cursorColor          = AppColor.primary,
            errorBorderColor     = AppColor.danger,
        ),
    )
}

private fun formatIndianDigits(rawDigits: String): String {
    val clean = rawDigits.filter { it.isDigit() }
    val n = clean.toLongOrNull() ?: return clean
    if (n == 0L) return ""
    return java.text.NumberFormat.getNumberInstance(java.util.Locale("en", "IN")).format(n)
}

@Composable
private fun BookingMoneyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "0",
    isError: Boolean = false,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label, style = bodyM) },
        placeholder    = { Text(placeholder, style = bodyM, color = AppColor.text3) },
        prefix         = { Text("₹ ", style = bodyM, color = AppColor.text) },
        modifier       = modifier.fillMaxWidth(),
        singleLine     = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape          = RoundedCornerShape(Radius.field),
        isError        = isError,
        supportingText = if (supportingText != null) { { Text(supportingText, style = bodyM) } } else null,
        colors         = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColor.primary,
            unfocusedBorderColor = AppColor.outline,
            focusedLabelColor    = AppColor.primary,
            unfocusedLabelColor  = AppColor.text2,
            cursorColor          = AppColor.primary,
            errorBorderColor     = AppColor.danger,
        ),
    )
}
