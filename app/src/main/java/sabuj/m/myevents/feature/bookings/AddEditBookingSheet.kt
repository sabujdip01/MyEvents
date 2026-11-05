package sabuj.m.myevents.feature.bookings

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.core.icons.EventIconDisplay
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.labelS
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleM
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
@OptIn(ExperimentalMaterial3Api::class)
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
    onBookingMadeChange: (Boolean) -> Unit = {},
    onDueDateChange: (Long?) -> Unit,
    onSave: () -> Unit,
    onAddPayment: () -> Unit,
    onDeletePayment: (String) -> Unit,
) {
    val isEdit = sheetState is BookingSheetState.Edit
    val bookingType: String
    val bookingIcon: BookingIcon?
    val eventId: String
    val contactName: String
    val phone: String
    val totalAmountPaise: Long
    val bookingAmountPaise: Long
    val nextDueDateEpochDay: Long?
    val bookingMade: Boolean
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
            bookingMade = sheetState.bookingMade
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
            bookingMade = sheetState.bookingMade
            eventError = sheetState.eventError
            totalError = sheetState.totalError; payments = sheetState.payments
        }
        else -> return // Hidden — nothing to show
    }

    val paidPaise = payments.sumOf { it.amountPaise }
    val amountLeftPaise = totalAmountPaise - paidPaise
    val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var showIconPicker by remember { mutableStateOf(false) }

    if (showIconPicker) {
        BookingIconPickerDialog(
            selectedIcon = bookingIcon,
            onSelect     = onIconChange,
            onDismiss    = { showIconPicker = false },
        )
    }

    val noBounceNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset = available
            override suspend fun onPreFling(available: Velocity): Velocity = Velocity.Zero
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity = available
        }
    }

    ModalBottomSheet(
        onDismissRequest    = onDismiss,
        sheetState          = modalSheetState,
        containerColor      = AppColor.surface,
        shape               = RoundedCornerShape(topStart = Radius.sheet, topEnd = Radius.sheet),
        dragHandle          = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .nestedScroll(noBounceNestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Spacer(Modifier.height(Spacing.xs))

            // Sheet title
            Text(
                text  = if (isEdit) "Edit Booking" else "Add Booking", // TODO strings.xml
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

            // Booking icon dropdown field (opens 5x9 icon picker popup)
            BookingIconDropdown(
                selectedIcon = bookingIcon,
                onClick      = { showIconPicker = true },
            )

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
            BookingMoneyTextField(
                paiseValue    = totalAmountPaise,
                onPaiseChange = onTotalChange,
                label         = "Contract Amount (₹)",
                placeholder   = "0",
                isError       = totalError != null,
                supportingText = totalError,
            )

            // Booking Amount / Advance (only in Add mode)
            if (!isEdit) {
                BookingMoneyTextField(
                    paiseValue    = bookingAmountPaise,
                    onPaiseChange = { amount ->
                        onBookingAmountChange(amount)
                        if (amount > 0L) {
                            onBookingMadeChange(true)
                        }
                    },
                    label         = "Booking Amount / Advance (₹)",
                    placeholder   = "0",
                )
            }

            // Booking Confirmed? Yes/No toggle (Moved just after Booking Amount field)
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = "Booking Confirmed?",
                    style    = labelL,
                    color    = AppColor.text,
                    modifier = Modifier.weight(1f),
                )
                androidx.compose.material3.Switch(
                    checked         = bookingMade,
                    onCheckedChange = onBookingMadeChange,
                    colors          = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor    = AppColor.onPrimary,
                        checkedTrackColor    = AppColor.primary,
                        uncheckedThumbColor  = AppColor.text3,
                        uncheckedTrackColor  = AppColor.surfaceVariant,
                        uncheckedBorderColor = AppColor.outline,
                    ),
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
            value              = selectedEvent?.let { "${it.title} (${DateUtils.formatDisplay(it.date)})" } ?: "",
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
                    text    = { Text("${event.title} (${DateUtils.formatDisplay(event.date)})", style = bodyM) },
                    onClick = { onSelect(event.id); expanded = false },
                    leadingIcon = {
                        EventIconDisplay(
                            icon               = event.icon,
                            contentDescription = event.title,
                            modifier           = Modifier.size(20.dp),
                        )
                    },
                )
            }
        }
    }
}

/** Booking icon selector field matching the For Event dropdown style. */
@Composable
private fun BookingIconDropdown(
    selectedIcon: BookingIcon?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value         = selectedIcon?.displayName ?: "",
            onValueChange = {},
            readOnly      = true,
            label         = { Text("Booking icon", style = bodyM) },
            placeholder   = { Text("Select booking icon", style = bodyM, color = AppColor.text3) },
            leadingIcon   = selectedIcon?.let { icon ->
                {
                    IconChip(
                        icon               = icon.fallback,
                        contentDescription = icon.displayName,
                        size               = IconChipSize.Sm,
                        customRes          = icon.customRes,
                    )
                }
            },
            trailingIcon  = {
                Icon(
                    imageVector        = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Select icon",
                    tint               = AppColor.text2,
                )
            },
            modifier       = Modifier.fillMaxWidth(),
            shape          = RoundedCornerShape(Radius.field),
            colors         = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppColor.primary,
                unfocusedBorderColor = AppColor.outline,
                focusedLabelColor    = AppColor.primary,
                unfocusedLabelColor  = AppColor.text2,
                cursorColor          = AppColor.primary,
            ),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(Radius.field))
                .clickable { onClick() }
        )
    }
}

/** Popup dialog showing 47 booking icons in a 5 col grid (increased width by 20%). */
@Composable
fun BookingIconPickerDialog(
    selectedIcon: BookingIcon?,
    onSelect: (BookingIcon) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            shape    = RoundedCornerShape(24.dp),
            colors   = CardDefaults.cardColors(containerColor = AppColor.surface),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = Spacing.md),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text     = "Select Booking Icon",
                        style    = titleM,
                        color    = AppColor.text,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector        = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint               = AppColor.text2,
                        modifier           = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() },
                    )
                }
                Spacer(Modifier.height(Spacing.md))

                // 47 icons in 5 columns grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier            = Modifier.verticalScroll(rememberScrollState()),
                ) {
                    BookingIcon.pickerEntries.chunked(5).forEach { rowIcons ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        ) {
                            rowIcons.forEach { icon ->
                                val isSelected = icon == selectedIcon
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(Radius.field))
                                        .background(if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant.copy(alpha = 0.5f))
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) AppColor.primary else Color.Transparent,
                                            shape = RoundedCornerShape(Radius.field),
                                        )
                                        .clickable {
                                            onSelect(icon)
                                            onDismiss() // Auto-close on selection
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    IconChip(
                                        icon               = icon.fallback,
                                        contentDescription = icon.displayName,
                                        size               = IconChipSize.Sm,
                                        containerColor     = Color.Transparent,
                                        iconColor          = if (isSelected) AppColor.primary else AppColor.text2,
                                        customRes          = icon.customRes,
                                    )
                                }
                            }
                            repeat(5 - rowIcons.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
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
    paiseValue: Long,
    onPaiseChange: (Long) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "0",
    isError: Boolean = false,
    supportingText: String? = null,
) {
    val rawDigits = if (paiseValue == 0L) "" else (paiseValue / 100L).toString()
    val formattedText = if (rawDigits.isNotEmpty()) formatIndianDigits(rawDigits) else ""

    var textFieldValue by remember(paiseValue) {
        mutableStateOf(TextFieldValue(text = formattedText, selection = TextRange(formattedText.length)))
    }

    OutlinedTextField(
        value         = textFieldValue,
        onValueChange = { input ->
            val digits = input.text.filter { it.isDigit() }.take(10)
            val newPaise = digits.toLongOrNull()?.times(100L) ?: 0L
            val formatted = if (digits.isNotEmpty()) formatIndianDigits(digits) else ""
            textFieldValue = TextFieldValue(
                text      = formatted,
                selection = TextRange(formatted.length),
            )
            onPaiseChange(newPaise)
        },
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
