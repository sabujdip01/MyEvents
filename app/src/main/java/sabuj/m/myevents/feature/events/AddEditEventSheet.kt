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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

/**
 * Bottom sheet for adding or editing an event.
 *
 * Simplifications per user feedback:
 *  - No separate "Title" field — event name is used as both title and name.
 *  - No date picker — the date is fixed from the calendar long-press and shown read-only.
 *  - Budget is formatted with Indian grouping as the user types (₹ prefix shown).
 *  - [saveError] non-null renders an inline red error box above the buttons.
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

    val selectedDate = if (existingEvent != null) existingEvent.date else prefilledDate

    // Form state
    var eventName by remember { mutableStateOf(existingEvent?.eventName ?: "") }
    var selectedIcon by remember { mutableStateOf(existingEvent?.icon ?: EventIcon.Wedding) }
    var budgetRaw by remember {                                         // digits only, no commas
        mutableStateOf(
            if (existingEvent != null) (existingEvent.budgetPaise / 100L).toString() else ""
        )
    }
    var inviteesRequired by remember { mutableStateOf(existingEvent?.inviteesRequired ?: true) }

    val isFormValid = eventName.isNotBlank()
    val budgetPaise = budgetRaw.toLongOrNull()?.let { it * 100L } ?: 0L

    /** Format raw digits (no commas) as Indian grouping for display while typing. */
    fun formatIndian(raw: String): String {
        val n = raw.toLongOrNull() ?: return raw
        return NumberFormat.getNumberInstance(Locale("en", "IN")).format(n)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = AppColor.surface,
        shape            = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
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

            // Event name — single field, used as both title and eventName on save
            AppTextField(
                value         = eventName,
                onValueChange = { eventName = it },
                label         = "Event name",
                placeholder   = "e.g. Wedding, Engagement",
            )

            // Date — read-only label (user picked date by long-pressing the calendar cell)
            AppTextField(
                value         = DateUtils.formatDisplay(selectedDate),
                onValueChange = {},
                label         = "Date",
                readOnly      = true,
            )

            // Event icon picker
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

            // Budget field — shows Indian-formatted number; stores raw digits
            OutlinedTextField(
                value         = formatIndian(budgetRaw),
                onValueChange = { input ->
                    // Strip commas and non-digits; cap at 10 digits
                    budgetRaw = input.filter { it.isDigit() }.take(10)
                },
                label         = { Text("Budget (₹)", style = bodyM) },
                placeholder   = { Text("0", style = bodyM, color = AppColor.text3) },
                prefix        = { Text("₹", style = bodyM, color = AppColor.text2) },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape         = RoundedCornerShape(14.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AppColor.primary,
                    unfocusedBorderColor = AppColor.outline,
                    focusedLabelColor    = AppColor.primary,
                    unfocusedLabelColor  = AppColor.text2,
                    cursorColor          = AppColor.primary,
                ),
            )

            // Invitees required toggle
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
                        checkedThumbColor    = AppColor.onPrimary,
                        checkedTrackColor    = AppColor.primary,
                        uncheckedThumbColor  = AppColor.text3,
                        uncheckedTrackColor  = AppColor.surfaceVariant,
                        uncheckedBorderColor = AppColor.outline,
                    ),
                )
            }

            // Inline error (e.g. date already taken)
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

            // Action buttons
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
                        if (isFormValid) {
                            // Use eventName as title so domain model stays intact
                            onSave(eventName, eventName, selectedDate, selectedIcon, budgetPaise, inviteesRequired)
                        }
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
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, style = bodyM) },
        placeholder   = { Text(placeholder, style = bodyM, color = AppColor.text3) },
        modifier      = modifier.fillMaxWidth(),
        readOnly      = readOnly,
        singleLine    = true,
        shape         = RoundedCornerShape(14.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColor.primary,
            unfocusedBorderColor = AppColor.outline,
            focusedLabelColor    = AppColor.primary,
            unfocusedLabelColor  = AppColor.text2,
            cursorColor          = AppColor.primary,
            disabledBorderColor  = AppColor.outlineVariant,
            disabledLabelColor   = AppColor.text3,
        ),
    )
}
