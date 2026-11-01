package sabuj.m.myevents.feature.events

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.icons.EventIcon
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.ui.IconChip
import sabuj.m.myevents.core.ui.IconChipSize
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.domain.model.Event
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Bottom sheet for adding or editing an event.
 * [existingEvent] null → Add mode; non-null → Edit mode.
 * [prefilledDate] is the initial date; defaults to today in Add mode.
 * [saveError] non-null shows an inline error (e.g. date already taken).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditEventSheet(
    prefilledDate: LocalDate,
    existingEvent: Event?,
    onDismiss: () -> Unit,
    saveError: String? = null,
    onSave: (title: String, eventName: String, date: LocalDate, icon: EventIcon, budgetPaise: Long, inviteesRequired: Boolean) -> Unit,
) {
    val isEdit = existingEvent != null
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Use today as default when prefilledDate is somehow null-equivalent (add from Home)
    val defaultDate = if (existingEvent != null) existingEvent.date else prefilledDate

    // Form state
    var title by remember { mutableStateOf(existingEvent?.title ?: "") }
    var eventName by remember { mutableStateOf(existingEvent?.eventName ?: "") }
    var selectedDate by remember { mutableStateOf(defaultDate) }
    var selectedIcon by remember { mutableStateOf(existingEvent?.icon ?: EventIcon.Wedding) }
    // Budget stored as plain rupee string (digits only) for input; formatted for display
    var budgetRupees by remember {
        mutableStateOf(
            if (existingEvent != null) (existingEvent.budgetPaise / 100L).toString() else ""
        )
    }
    var inviteesRequired by remember { mutableStateOf(existingEvent?.inviteesRequired ?: true) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isFormValid = title.isNotBlank() && eventName.isNotBlank()
    val budgetPaise = budgetRupees.replace(",", "").toLongOrNull()?.let { it * 100L } ?: 0L

    ModalBottomSheet(
        onDismissRequest    = onDismiss,
        sheetState          = sheetState,
        containerColor      = AppColor.surface,
        shape               = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
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
                text  = if (isEdit) "Edit Event" else "Add Event",
                style = titleL,
                color = AppColor.text,
            )

            // Title field
            AppTextField(
                value         = title,
                onValueChange = { title = it },
                label         = "Event title",
                placeholder   = "e.g. Riya & Raj",
            )

            // Event name field
            AppTextField(
                value         = eventName,
                onValueChange = { eventName = it },
                label         = "Event name",
                placeholder   = "e.g. Wedding",
            )

            // Date picker (read-only field, opens DatePickerDialog on tap)
            AppTextField(
                value         = DateUtils.formatDisplay(selectedDate),
                onValueChange = {},
                label         = "Date",
                readOnly      = true,
                trailingIcon  = {
                    Icon(
                        imageVector        = Icons.Rounded.DateRange,
                        contentDescription = "Pick date",
                        tint               = AppColor.primary,
                        modifier           = Modifier.clickable { showDatePicker = true },
                    )
                },
            )

            // Event icon picker — FlowRow of 52dp chips
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(text = "Event icon", style = labelM, color = AppColor.text2)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalArrangement   = Arrangement.spacedBy(Spacing.sm),
                ) {
                    EventIcon.entries.forEach { icon ->
                        val isSelected = icon == selectedIcon
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) AppColor.primary else AppColor.surface,
                                    shape = RoundedCornerShape(14.dp),
                                )
                                .clickable { selectedIcon = icon },
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
            }

            // Budget field — digits only; display with Indian grouping (₹ prefix not shown in field)
            AppTextField(
                value         = budgetRupees,
                onValueChange = { raw ->
                    // Keep only digits; cap length at 10 to avoid overflow
                    budgetRupees = raw.filter { it.isDigit() }.take(10)
                },
                label         = "Budget (₹)",
                placeholder   = "0",
                keyboardType  = KeyboardType.Number,
                prefix        = { Text("₹", style = bodyM, color = AppColor.text2) },
            )

            // Invitees required toggle (replaces segmented control — simpler)
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = "Invitees required",
                    style    = labelL,
                    color    = AppColor.text,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked         = inviteesRequired,
                    onCheckedChange = { inviteesRequired = it },
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor       = AppColor.onPrimary,
                        checkedTrackColor       = AppColor.primary,
                        uncheckedThumbColor     = AppColor.text3,
                        uncheckedTrackColor     = AppColor.surfaceVariant,
                        uncheckedBorderColor    = AppColor.outline,
                    ),
                )
            }

            // Inline error (e.g. date already taken — shown above buttons so user sees it)
            if (saveError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColor.dangerContainer)
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                ) {
                    Text(text = saveError, style = bodyM, color = AppColor.danger)
                }
            }

            // Action buttons — Cancel (ghost) | Add/Save (primary)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Button(
                    onClick  = onDismiss,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = AppColor.primaryContainer,
                        contentColor   = AppColor.onPrimaryContainer,
                    ),
                    shape    = RoundedCornerShape(16.dp),
                ) { Text("Cancel", style = labelL) }

                Button(
                    onClick  = {
                        if (isFormValid) onSave(title, eventName, selectedDate, selectedIcon, budgetPaise, inviteesRequired)
                    },
                    enabled  = isFormValid,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = AppColor.primary,
                        contentColor   = AppColor.onPrimary,
                    ),
                    shape    = RoundedCornerShape(16.dp),
                ) { Text(if (isEdit) "Save" else "Add", style = labelL) }
            }

            Spacer(Modifier.height(Spacing.xl))
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = state)
        }
    }
}

/** Reusable styled text field for the Add/Edit sheet. */
@Composable
private fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, style = bodyM) },
        placeholder   = { Text(placeholder, style = bodyM, color = AppColor.text3) },
        modifier      = modifier.fillMaxWidth(),
        readOnly      = readOnly,
        trailingIcon  = trailingIcon,
        prefix        = prefix,
        singleLine    = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape         = RoundedCornerShape(14.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColor.primary,
            unfocusedBorderColor = AppColor.outline,
            focusedLabelColor    = AppColor.primary,
            unfocusedLabelColor  = AppColor.text2,
            cursorColor          = AppColor.primary,
        ),
    )
}
